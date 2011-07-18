package controllers

import play._
import play.mvc._

object Application extends Controller {

    import views.Application._

    def index = html.index()
    def include = html.include()
    def includeError = html.includeError()
    def inline = html.inline()
    def inlineError = html.inlineError()

}
