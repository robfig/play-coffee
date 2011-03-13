package play.modules.coffee;

import java.util.ArrayList;
import java.util.List;

import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.JCoffeeScriptCompileException;

import java.io.PrintStream;
import play.Play;
import play.PlayPlugin;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.vfs.VirtualFile;
import play.PlayPlugin;
import play.templates.FastTags.Namespace;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * This plugin intercepts requests for static files ending in '.coffee', and
 * serves the compiled javascript instead.
 */
public class CoffeePlugin extends PlayPlugin {

    private JCoffeeScriptCompiler compiler;

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
        } catch(Exception e) {
            response.contentType = "text/javascript";
            response.status = 500;
            response.print("Oops,\n");
            e.printStackTrace(new PrintStream(response.out));
        }
        return true;
    }
}
