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

    private static final class CompiledCoffee {
        public final Long sourceLastModified;  // Last modified time of the VirtualFile
        public final String output;  // Compiled coffee

        public CompiledCoffee(Long sourceLastModified, String output) {
            this.sourceLastModified = sourceLastModified;
            this.output = output;
        }
    }

    // Regex to get the line number of the failure.
    private static final Pattern LINE_NUMBER = Pattern.compile("line ([0-9]+)");
    private static final ThreadLocal<JCoffeeScriptCompiler> compiler =
        new ThreadLocal<JCoffeeScriptCompiler>() {
            @Override protected JCoffeeScriptCompiler initialValue() {
                return new JCoffeeScriptCompiler(); }};
    private Map<String, CompiledCoffee> cache;  // Map of Relative Path -> Compiled coffee

    /** @return the line number that the exception happened on, or 0 if not found in the message. */
    public static int getLineNumber(JCoffeeScriptCompileException e) {
        Matcher m = LINE_NUMBER.matcher(e.getMessage());
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public static JCoffeeScriptCompiler getCompiler() {
        return compiler.get();
    }

    @Override
    public void onLoad() {
        cache = new HashMap<String, CompiledCoffee>();
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

            // Check the cache.
            String relativePath = file.relativePath();
            CompiledCoffee cc = cache.get(relativePath);
            if (cc != null && cc.sourceLastModified.equals(file.lastModified())) {
                response.print(cc.output);
                return true;
            }

            // Compile the coffee and return.
            String compiledCoffee = getCompiler().compile(file.contentAsString());
            cache.put(relativePath, new CompiledCoffee(file.lastModified(), compiledCoffee));
            response.print(compiledCoffee);
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
