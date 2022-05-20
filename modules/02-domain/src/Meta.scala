package domain

import play.api.i18n.Lang
import utils.m

case class Meta(
  title:       Option[String]             = None,
  sub_title:   Option[String]             = None,
  description: Option[String]             = None,
  documents:   Option[List[MetaDocument]] = None,
  args:        Map[String, List[String]]  = Map.empty
) {
  def translate()(implicit lang: Lang): Meta = {
    Meta(
      title.map(m(_, args.getOrElse("title", List.empty))),
      sub_title.map(m(_, args.getOrElse("sub_title", List.empty))),
      description.map(m(_, args.getOrElse("description", List.empty))),
      documents
    )
  }
}

case class MetaDocument(name: String, url: String)
