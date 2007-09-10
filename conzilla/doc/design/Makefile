DOC       := conzilla-design
DEPS	  := $(wildcard ch-*.xml)

html:    out/${DOC}/${DOC}.html
onehtml: out/${DOC}.html
ps:      out/${DOC}.ps


out/${DOC}/${DOC}.html: ${DOC}.xml ${DEPS}
	@ mkdir out out/${DOC} 2>/dev/null; exit 0
	@ cp stylesheets/conzilla.css out/${DOC}; exit 0
	cd out/${DOC};\
	jade -t sgml -d ../../stylesheets/html.dsl ../../${DOC}.xml 

out/${DOC}.html: ${DOC}.xml ${DEPS}
	@ mkdir out 2>/dev/null; exit 0
	@ cp stylesheets/conzilla.css out; exit 0
	cd out;\
	jade -t sgml -d ../stylesheets/html.dsl#onehtml ../${DOC}.xml \
           > ${DOC}.html

out/${DOC}.ps: ${DOC}.xml ${DEPS}
	cd out; \
	jade -t tex -V tex-backend -d ../stylesheets/print.dsl -o ${DOC}.tex ../${DOC}.xml; \
	jadetex ${DOC}.tex;\
	jadetex ${DOC}.tex;\
	jadetex ${DOC}.tex;\
	dvips -o ${DOC}.ps ${DOC}.dvi

clean:
	rm -rf out/*
