package tags;

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.exceptions.TemplateExecutionException;
import play.templates.FastTags.Namespace;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;
import play.templates.Template;

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
            // Show a nice compilation error message.
            // All of the exceptions say "(Type of error) on line 54: (Error detail)"
            play.Logger.error(e, "Coffee compilation error");
            String error = e.getMessage();
            int i = error.indexOf("line ");
            int coffeeLine = Integer.parseInt(error.substring(i + 5, error.indexOf(":", i)));
            throw new TemplateExecutionException(
                template.template, fromLine + coffeeLine, e.getMessage(), e);
        }
    }
}
