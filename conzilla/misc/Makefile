# $Id$
#############################################################################
#
#                Makefile for the Conzilla project.
#                ==================================
#
#
#  This Makefile generally should not need to be modified. All local
#  configuration of the build environment is made externally.
#
#
#  Targets:
#  ~~~~~~~~
#
#  * classes   -- (default) will compile the classes in
#                 $(CONZILLA_CLASSES) and $(TEST_CLASSES), and copy
#                 the resource/ directory to classes/resource.
#
#  * jar       -- as 'classes', plus creates the JAR file 'Conzilla.jar' in
#                 classes/.
#
#  * signedjar -- as 'jar', plus signs the JAR file and names the new one
#                 'ConzillaS.jar'/.
#
#  * doc       -- will make all JavaDoc documentation and place it in
#                 javadoc/ (which will be created).
#
#  * clean     -- will remove anything under classes/.
#
#  * docclean  -- will remove anything under javadoc/.
#
#
#
#  Steps to take to ensure the perfect working of this Makefile:
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#  * Make sure it is placed in one of the subdirectories of the 
#    Conzilla root directory. Default is misc/.
#
#  * Make sure 'make' is always called standing in that directory,
#    for example by always using 'make -C [conzilla-root]/misc'.
#
#  * Make sure the JAR files lined up below as $(EXTERNALJARFILES) are
#    available in the misc/ directory, either as files or as symbolic links.
#
#  * Make sure the JAVAC variable has a reasonable value.
#
#  * Make sure the given compiler is in the PATH, together with:
#    find, egrep, sed, jar, jarsigner and javadoc.
#
#
#
#  Other files used by this Makefile.
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#  * install/*         -- resources used by install. Will be included
#                         in JAR file!
#
#  * misc/cidkeys      -- Java keystore used for signing.
#
#  * misc/package-list -- list of packages in JDK-1.2, to generate doc links.
#
#
#
#  Comments to:
#
#    Mikael Nilsson <mikael.nilsson@bigfoot.com>
#  
#############################################################################


#############################################################################
#
#  Directories.
#  ~~~~~~~~~~~~
#
#  In general, these should not be edited.
#
#############################################################################

CONZILLA_HOME  := $(shell cd ..; pwd)
DOCDIR         := ${CONZILLA_HOME}/javadoc
CLASSDIR       := ${CONZILLA_HOME}/classes
PACKAGEDIR     := ${CONZILLA_HOME}/src
RESOURCEDIR    := ${CONZILLA_HOME}/resource
MISCDIR        := ${CONZILLA_HOME}/misc


#############################################################################
#
#  'Main' classes.
#  ~~~~~~~~~~~~~~~
#
#  Make sure these are reasonable.
#  These are the (only) classes that will be compiled.
#
#############################################################################

CONZILLA_CLASSES = $(wildcard ${PACKAGEDIR}/se/kth/cid/conzilla/app/*.java) \
                   $(wildcard ${PACKAGEDIR}/se/kth/cid/conzilla/install/*.java)
TEST_CLASSES     = $(wildcard ${PACKAGEDIR}/se/kth/cid/test/*.java)


#############################################################################
#
#  Compiler.
#  ~~~~~~~~~
#
#  Edit as needed. Be sure to use a dependency check flag of some sort.
#  Will need to support -d and -classpath
#
#############################################################################

JAVAC          := jikes +P +E -Xdepend +F -g
#JAVAC          := javac -Xdepend


#############################################################################
#
#  External JAR files needed.
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#  Edit when dependencies change. These files will be looked for in
#  $(MISCDIR).
#
#############################################################################

EXTERNALJARFILES := rt.jar  aelfred.jar  jnlp.jar  capsapi.jar   ftp.jar



#############################################################################
#
#  Conzilla version
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#  Edit as necessary. Will be put in $(ABOUT_MESSAGEFILE) (a neuron)
#  and in CONZILLA_CONFIGFILE.
#
#
#############################################################################

CONZILLA_VERSION := 1.0


############################################################################
#
#
#
#
#
#  The rest of this file should not be changed.
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#
#
#
#############################################################################

empty:=
space:= $(empty) $(empty)
EXTERNALJARS := $(subst ${space},,${EXTERNALJARFILES:%=${MISCDIR}/%:})

JAVACOMPILE := $(JAVAC) -d $(CLASSDIR) \
                        -classpath ${CLASSDIR}:$(EXTERNALJARS)$(PACKAGEDIR)

PACKAGES    = $(shell cd $(PACKAGEDIR);                 \
                      find se/kth/cid -type d           \
                      | egrep -v 'CVS|cid$$|conzilla$$' \
                      | sed -e "s|/|.|g")

ABOUT_MESSAGEFILE := /components/maps/about/message
CONZILLA_CONFIGFILE := $(PACKAGEDIR)/se/kth/cid/conzilla/install/ConzillaConfig.java
USER_INFO := $(shell id -un)
HOST_INFO := $(shell uname -n)

CONZILLA_FULL_VERSION_INFO := $(USER_INFO)@$(HOST_INFO) $(shell date "+%Y-%m-%d %T")




all: classes resources extjars

version:
	cp $(CONZILLA_CONFIGFILE) $(CONZILLA_CONFIGFILE).tmp
	sed 's/CONZILLA_VERSION = ".*"/CONZILLA_VERSION = "$(CONZILLA_VERSION)"/g' \
		$(CONZILLA_CONFIGFILE).tmp > $(CONZILLA_CONFIGFILE)

classes: version
	${JAVACOMPILE} ${CONZILLA_CLASSES}

test: classes
	${JAVACOMPILE} ${TEST_CLASSES}

resources:
	cp -R ${RESOURCEDIR}/* $(CLASSDIR)
	cd $(CLASSDIR)/components; tar xzf $(MISCDIR)/localhelp.tgz 
	find $(CLASSDIR) -type d -name CVS | xargs rm -rf 
	sed -e 's/CONZILLA_VERSION/$(CONZILLA_VERSION)/g' -e 's/CONZILLA_FULL_VERSION_INFO/$(CONZILLA_FULL_VERSION_INFO)/g' \
		${RESOURCEDIR}$(ABOUT_MESSAGEFILE) >  $(CLASSDIR)$(ABOUT_MESSAGEFILE)

extjars:
	cd ${CLASSDIR};                    \
	jar xf $(MISCDIR)/aelfred.jar com; \
	jar xf $(MISCDIR)/ftp.jar com


jar: all
	cd ${CLASSDIR};                    \
	rm -rf se/kth/cid/test;            \
	jar cf Conzilla.jar se com components install properties

signedjar: jar
	jarsigner -keystore $(MISCDIR)/cidkeys -storepass testkey \
		  -signedjar $(CLASSDIR)/ConzillaS.jar            \
		   $(CLASSDIR)/Conzilla.jar testkey

upload: signedjar
	scp $(CLASSDIR)/ConzillaS.jar conzilla.sf.net:/home/groups/c/co/conzilla/htdocs/execs/javaws/
	scp $(CLASSDIR)/Conzilla.jar conzilla.sf.net:/home/groups/c/co/conzilla/htdocs/download/


doc: 
	-mkdir ${DOCDIR}
	javadoc 		               \
	-classpath ${CLASSDIR}:$(EXTERNALJARS) \
	-sourcepath ${PACKAGEDIR}              \
        -d ${DOCDIR}                           \
        -use                                   \
        -splitIndex                            \
        -windowtitle $(WINDOWTITLE)            \
        -doctitle $(DOCTITLE)                  \
        -header $(HEADER)                      \
        -bottom $(BOTTOM)                      \
        -group $(GROUPUTIL)                    \
        -group $(GROUPCORE)                    \
        -group $(GROUPUI)                      \
        -group $(GROUPTEST)                    \
	-version                               \
	-author                                \
	-linkoffline $(JAVADOCROOT) $(MISCDIR) \
	${PACKAGES}

# Could be used:
#       -overview $(PACKAGEDIR)/overview.html
#       -J-Xmx180m                       

JAVADOCROOT = "http://java.sun.com/products/jdk/1.2/docs/api"
#JAVADOCROOT = "/usr/local/doc/jdk1.2.2/docs/api/"
WINDOWTITLE = "Conzilla 1.0 API Documentation"
DOCTITLE    = "Conzilla 1.0 API Documentation"
HEADER      = "<b>Conzilla 1.0</b><br><font size=\"-1\">Draft</font>"
BOTTOM      = "<font size=\"-1\"><a href=\"http://www.nada.kth.se/cid\">Centre for user-oriented IT -design</a><br><br></font>"
GROUPUTIL   = "Utility Packages" "se.kth.cid.util:se.kth.cid.xml:se.kth.cid.identity*:se.kth.cid.conzilla.util"
GROUPCORE   = "Core Packages" "se.kth.cid*"
GROUPUI     = "Browser UI Packages" "se.kth.cid.conzilla*"
GROUPTEST   = "Test Packages" "se.kth.cid.test*"

clean: 
	rm -rf $(CLASSDIR)/*

docclean:
	rm -rf $(DOCDIR)/*

