package tags;

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.exceptions.TemplateExecutionException;
import play.modules.coffee.CoffeePlugin;
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
        String coffee = JavaExtensions.toString(body)
            .replace("#\\{", "#{");  // String interpolation
        try {
            String js = CoffeePlugin.getCompiler().compile(coffee);
            out.print("<script type=\"text/javascript\">\n");
            out.print(js);
            out.print("</script>");
        } catch (JCoffeeScriptCompileException e) {
            // Show a nice compilation error message.
            play.Logger.error(e, "Coffee compilation error");
            fromLine += CoffeePlugin.getLineNumber(e);
            throw new TemplateExecutionException(
                template.template, fromLine, e.getMessage(), e);
        }
    }
}
