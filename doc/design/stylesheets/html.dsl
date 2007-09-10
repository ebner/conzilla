<!--
    html.dsl - stylesheets for HTML output.

    $Id$

    Adapted from SGMLtools - an SGML toolkit.
    Copyright (C) 1998 Cees A. de Groot
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
  
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
  
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  
  -->
<!DOCTYPE style-sheet PUBLIC "-//James Clark//DTD DSSSL Style Sheet//EN" [
<!ENTITY docbook.dsl PUBLIC "-//Norman Walsh//DOCUMENT DocBook HTML Stylesheet//EN" CDATA dsssl>
]>
<style-sheet>

<style-specification id="html" use="docbook">
<style-specification-body> 
    ;;
    ;;  This is the standard HTML stylesheet, which deviates only a little bit
    ;;  from the plain version.
    ;;
    (define (toc-depth nd)
      (if (string=? (gi nd) (normalize "book"))
         2
         1))
    (define bibltable #t)
    (define biblio-citation-check #t)
    (define %show-comments% #t)
    (define %stylesheet% "conzilla.css")
    (define %html-ext% ".html")
    (define %body-attr% (list
	  (list "BGCOLOR" "#FFFFFF")
	  (list "TEXT" "#000000")
	  (list "LINK" "#0000FF")
	  (list "VLINK" "#840084")
	  (list "ALINK" "#0000FF")))
    (define %shade-verbatim% #t)
    (define %use-id-as-filename% #t)
    (define %generate-book-titlepage% #t)
    (define (book-titlepage-recto-elements)
	'("title" "subtitle" "author" "address" "orgname" "edition"
	  "releaseinfo" "copyright" "keywordset" "abstract"))
    (define (book-titlepage-verso-elements) '())

    (define %graphic-default-extension% "gif")
    (define %admon-graphics% #t)
    (define %gentext-nav-tblwidth% "100%")

    (define ($myparagraph$)
      (make sequence 
	    (make element gi: "A"
		  attributes: (list
			       (list "NAME" (element-id)))
		  (empty-sosofo))
	    ($paragraph$)))
    (element para ($myparagraph$))

</style-specification-body>
</style-specification>

<style-specification id="onehtml" use="html">
<style-specification-body>
    ;;
    ;;  Spit out a single file.
    ;;
    (define nochunks #t)
</style-specification-body>
</style-specification>

<style-specification id="howto" use="html">
<style-specification-body>
    ;;
    ;;  This stylesheet tries to get a bit closer to what people are used to from
    ;;  SGMLtools 1.0.
    ;;
    (define %generate-article-toc% #t)
</style-specification-body>
</style-specification>

<external-specification id="docbook" document="docbook.dsl">
</style-sheet>
