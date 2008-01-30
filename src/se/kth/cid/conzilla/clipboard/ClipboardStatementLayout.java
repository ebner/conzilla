/*  $Id: $
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */
package se.kth.cid.conzilla.clipboard;

import se.kth.cid.layout.StatementLayout;
import se.kth.cid.layout.ContextMap.BoundingBox;
import se.kth.cid.layout.ContextMap.Position;

public class ClipboardStatementLayout extends ClipboardDrawerLayout {
	Position[] boxLine;
	int boxLinePathType;
	Position[] line;
	BoundingBox lbb;
	String objectCDL;
	String subjectCDL;
	boolean isLiteral;
	int pathType;
	
	public ClipboardStatementLayout(StatementLayout sl) {
		super(sl);
		boxLine = sl.getBoxLine();
		boxLinePathType = sl.getBoxLinePathType();
		line = sl.getLine();
		lbb = sl.getLiteralBoundingBox();
		objectCDL = sl.getObjectLayoutURI();
		subjectCDL = sl.getSubjectLayoutURI();
		isLiteral = sl.isLiteralStatement();
		pathType = sl.getPathType();
	}

	public Position[] getBoxLine() {
		return boxLine;
	}

	public int getBoxLinePathType() {
		return boxLinePathType;
	}

	public Position[] getLine() {
		return line;
	}

	public BoundingBox getLiteralBoundingBox() {
		return lbb;
	}

	public String getObjectLayoutURI() {
		return objectCDL;
	}

	public int getPathType() {
		return pathType;
	}

	public String getSubjectLayoutURI() {
		return subjectCDL;
	}

	public boolean isLiteralStatement() {
		return isLiteral;
	}
}
