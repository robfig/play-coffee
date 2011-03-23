package tags;

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Regex to get the line number of the failure.
    private static final Pattern LINE_NUMBER = Pattern.compile("line ([0-9]+)");

    public static void _inline(Map<?, ?> args,
                               Closure body,
                               PrintWriter out,
                               ExecutableTemplate template,
                               int fromLine) {
        String coffee = JavaExtensions.toString(body);
        try {
            String js = new JCoffeeScriptCompiler().compile(coffee);
            out.print("<script type=\"text/javascript\">\n");
            out.print(js);
            out.print("</script>");
        } catch (JCoffeeScriptCompileException e) {
            // Show a nice compilation error message.
            // All of the exceptions say "(Type of error) on line 54: (Error detail)"
            play.Logger.error(e, "Coffee compilation error");
            Matcher m = LINE_NUMBER.matcher(e.getMessage());
            if (m.find()) {
                fromLine += Integer.parseInt(m.group(1));
            }
            throw new TemplateExecutionException(
                template.template, fromLine, e.getMessage(), e);
        }
    }
}
