package play.modules.coffee;

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Play;
import play.PlayPlugin;
import play.exceptions.CompilationException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * This plugin intercepts requests for static files ending in '.coffee', and
 * serves the compiled javascript instead.
 */
public class CoffeePlugin extends PlayPlugin {

    // Regex to get the line number of the failure.
    private static final Pattern LINE_NUMBER = Pattern.compile("line ([0-9]+)");

    private JCoffeeScriptCompiler compiler;

    /** @return the line number that the exception happened on, or 0 if not found in the message. */
    public static int getLineNumber(JCoffeeScriptCompileException e) {
        Matcher m = LINE_NUMBER.matcher(e.getMessage());
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    @Override
    public void onLoad() {
        compiler = new JCoffeeScriptCompiler();
    }

    @Override
    public boolean serveStatic(VirtualFile file, Request request, Response response) {
        if (!file.getName().endsWith(".coffee")) {
            return super.serveStatic(file, request, response);
        }

        try {
            response.contentType = "text/javascript";
            response.status = 200;
            if (Play.mode == Play.Mode.PROD) {
                response.cacheFor("1h");
            }
            response.print(compiler.compile(file.contentAsString()));
        } catch (JCoffeeScriptCompileException e) {
            // Render a nice error page.
            Template tmpl = TemplateLoader.load("errors/500.html");
            Map<String, Object> args = new HashMap<String, Object>();
            Exception ex = new CompilationException(file, e.getMessage(), getLineNumber(e), -1, -1);
            args.put("exception", ex);
            play.Logger.error(ex, "Coffee compilation error");
            response.contentType = "text/html";
            response.status = 500;
            response.print(tmpl.render(args));
        }
        return true;
    }
}
