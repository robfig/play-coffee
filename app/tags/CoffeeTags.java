package tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.PrintStream;

import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.JCoffeeScriptCompileException;

import groovy.lang.Closure;
import play.PlayPlugin;
import play.templates.FastTags;
import play.templates.JavaExtensions;
import play.templates.FastTags.Namespace;
import play.templates.Template;
import play.vfs.VirtualFile;
import play.templates.GroovyTemplate.ExecutableTemplate;

/**
 * This file has Tag support for Coffee.
 */
@Namespace("coffee")
public class CoffeeTags extends FastTags {

    public static void _inline(Map<?, ?> args,
                               Closure body,
                               PrintWriter out,
                               ExecutableTemplate template,
                               int fromLine) {
        String coffee = JavaExtensions.toString(body);
        try {
            String js = new JCoffeeScriptCompiler().compile(coffee);
            out.print("<script>\n");
            out.print(js);
            out.print("</script>");
        } catch (JCoffeeScriptCompileException e) {
            play.Logger.error("Error: " + e);
            // TODO: Show a nice compilation error.
        }
    }
}
