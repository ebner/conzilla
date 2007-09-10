CLASSPATH = /usr/local/lib/jre1.3/lib/rt.jar:/usr/local/lib/capsapi.jar:/usr/local/lib/aelfred.jar
export CLASSPATH

CONZILLA_HOME  = $(HOME)/conzilla
DOCDIR         = $(CONZILLA_HOME)/javadoc
CLASSDIR       = $(CONZILLA_HOME)/classes
PACKAGE_HOME   = $(CONZILLA_HOME)/java

JAVAC          = jikes +E -g
#JAVAC          = javac
JDEPS          = jdeps
#####################################################

CLASSPATH  := $(CLASSDIR):$(CLASSPATH):$(PACKAGE_HOME)
JAVACOMPILE = $(JAVAC) -d $(CLASSDIR)

DEPSFILE    = $(PACKAGE_HOME)/.deps
SOURCESFILE = $(PACKAGE_HOME)/.sourcefiles


DTDSRCDIR   = $(CONZILLA_HOME)/dtd
DTDDESTDIR  = $(PACKAGE_HOME)/se/kth/cid/component/xml/dtd

DTDS     = $(wildcard $(DTDSRCDIR)/*.dtd)
JAVADTDS = $(DTDS:$(DTDSRCDIR)/%.dtd=$(DTDDESTDIR)/%DTD.java)

$(DTDDESTDIR)/%DTD.java: $(DTDSRCDIR)/%.dtd
	$(CONZILLA_HOME)/misc/genDTDClass.pl $(*)DTD < $< > $@ || (rm $@ && exit 1)


PACKAGES    = se.kth.cid.component                    \
	      se.kth.cid.component.cache	      \
	      se.kth.cid.component.local	      \
	      se.kth.cid.component.lookup	      \
	      se.kth.cid.component.xml		      \
	      se.kth.cid.component.xml.dtd	      \
	      se.kth.cid.conceptmap		      \
	      se.kth.cid.content		      \
	      se.kth.cid.conzilla		      \
	      se.kth.cid.conzilla.browse	      \
	      se.kth.cid.conzilla.content	      \
	      se.kth.cid.conzilla.controller	      \
	      se.kth.cid.conzilla.editmodule	      \
	      se.kth.cid.conzilla.edittool	      \
	      se.kth.cid.conzilla.history	      \
	      se.kth.cid.conzilla.mapmodule	      \
	      se.kth.cid.conzilla.mapmodule.graphics  \
	      se.kth.cid.conzilla.metadata	      \
	      se.kth.cid.conzilla.tool		      \
	      se.kth.cid.conzilla.util		      \
	      se.kth.cid.neuron			      \
	      se.kth.cid.neuron.local		      \
	      se.kth.cid.test			      \
	      se.kth.cid.util                         \
	      se.kth.cid.xml


.PHONY : all source deps jar docs

all: real_all

ifneq ($(MAKECMDGOALS),deps)
include $(DEPSFILE)
endif 

real_all: $(CLASSES)


jar:
	cd ../classes; \
	jar c0f ../html/Conzilla.jar se

docs: 
	javadoc -d $(DOCDIR) -private -version -author $(PACKAGES)

clean: 
	rm -f $(CLASSES)
	find $(CLASSDIR) -name "*.class" | xargs rm -f

realclean: clean
	rm -f $(DEPSFILE) $(SOURCESFILE)

makesource: $(JAVADTDS)
	find $(PACKAGE_HOME) -name '*.java' -print > $(SOURCESFILE)


# These are for forced generation of .deps and .sourcefiles


deps: makesource
	$(JDEPS) -d $(CLASSDIR) -f $(SOURCESFILE) > $(DEPSFILE)

# These make the generation of .deps and .sourcefiles automagic

$(SOURCESFILE): $(JAVADTDS)
	find $(PACKAGE_HOME) -name '*.java' -print > $(SOURCESFILE)

$(DEPSFILE) : $(SOURCESFILE)
	$(JDEPS) -d $(CLASSDIR) -f $(SOURCESFILE) > $(DEPSFILE)
