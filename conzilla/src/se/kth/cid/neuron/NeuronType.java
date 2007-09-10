/* $Id$ */
/*
  This file is part of the Conzilla browser, designed for
  the Garden of Knowledge project.
  Copyright (C) 1999  CID (http://www.nada.kth.se/cid)
  
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
*/


package se.kth.cid.neuron;
import se.kth.cid.neuron.*;
import se.kth.cid.util.*;
import se.kth.cid.component.*;

/** This is the interface representing NeuronTypes.
 *
 *  @author Mikael Nilsson
 *  @version $Revision$
 */
public interface NeuronType extends Component
{
  int FIRST_NEURONTYPE_EDIT_CONSTANT = Neuron.LAST_NEURON_EDIT_CONSTANT + 1;
  
  int BOXTYPE_EDITED                 = FIRST_NEURONTYPE_EDIT_CONSTANT;
  int LINETYPE_EDITED                = FIRST_NEURONTYPE_EDIT_CONSTANT + 1;
  int DATATAG_ADDED                  = FIRST_NEURONTYPE_EDIT_CONSTANT + 2;
  int DATATAG_REMOVED                = FIRST_NEURONTYPE_EDIT_CONSTANT + 3;
  int AXONTYPE_ADDED                 = FIRST_NEURONTYPE_EDIT_CONSTANT + 4;
  int AXONTYPE_REMOVED               = FIRST_NEURONTYPE_EDIT_CONSTANT + 5;
  
  int LAST_NEURONTYPE_ONLY_EDIT_CONSTANT  = AXONTYPE_REMOVED;
  int LAST_NEURONTYPE_EDIT_CONSTANT  = AxonType.LAST_AXONTYPE_EDIT_CONSTANT;


  class BoxType
  {
    /** The type of box. Recognized values must include:
     *
     *  rectangle, roundrectangle, diamond, ellipse, invisible.
     */
    public String  type;
    public boolean filled;
    public String  borderType;
    public int     borderThickness;

    public BoxType(String type, boolean filled, String borderType,
		   int borderThickness)
      {
	this.type            = type;
	this.filled          = filled;
	this.borderType      = borderType;
	this.borderThickness = borderThickness;
      }
  }

  class HeadType
  {
    /** The type of head. Recognized values must include:
     *
     *  arrow, varrow, sharparrow, bluntarrow, diamond, ellipse, none.
     */
    public String  type;
    public boolean filled;
    public int     width;
    public int     length;

    public HeadType(String type, boolean filled, int width,
		    int length)
      {
	this.type   = type;
	this.filled = filled;
	this.width  = width;
	this.length = length;
      }
  }
  
  class LineType
  {
    /** The type of line. Recognized values must include:
     *
     *  continuous, dotted, dashed, dashdot, dashdotdot, dashdotdotdot.
     */
    public String  type;
    public int     thickness;

    public LineType(String type, int thickness)
      {
	this.type      = type;
	this.thickness = thickness;
      }
  }
  
  /** Returns the box type of this neuron type.
   *
   *  @return the line type of this neuron type. Never null.
   */
  BoxType getBoxType();

  /** Sets the box type of this neuron type.
   *
   *  @param type the head type of this neuron type. Never null.
   *  @exception ReadOnlyException if the NeuronType
   *             was not editable.
   *  @exception NeuronException if the type was not valid.
   */
  void    setBoxType(BoxType type) throws ReadOnlyException, NeuronException;


  /** Returns the line type of this neuron type.
   *
   *  @return the line type of this neuron type. Never null.
   */
  LineType  getLineType();

  /** Sets the line type of this neuron type.
   *
   *  @param type the line type of this neuron type. Never null.
   *  @exception ReadOnlyException if the NeuronType
   *             was not editable.
   *  @exception NeuronException if the type was not valid.
   */
  void    setLineType(LineType type) throws ReadOnlyException, NeuronException;

  
  /** Returns the list of allowed data tags in this NeuronType.
   *
   *  @return the list of allowed data tags in this NeuronType. Never null,
   *          but may be empty.
   */
  String[] getDataTags();

  /** Adds a data tag to be allowed in this neuron type.
   *
   *  @param tag the tag to add.
   *  @exception ReadOnlyException if the NeuronType
   *             was not editable.
   */
  void     addDataTag(String tag) throws ReadOnlyException;

  /** Removes a data tag to be allowed in this neuron type.
   *
   *  @param tag the tag to remove.
   *  @exception ReadOnlyException if the NeuronType
   *             was not editable.
   */
  void     removeDataTag(String tag) throws ReadOnlyException;


  /** Returns the AxonTypes in this NeuronType.
   *
   *  @return the AxonTypes in this NeuronType. Never null, but may be empty.
   */
  AxonType[] getAxonTypes();

  /** Returns the AxonType with the given type name,
   *  or null if no such AxonType is found.
   *
   *  @param type the type name of the AxonType.
   *  @return the Axon with the given type name.
   */
  AxonType   getAxonType(String type);


  /** Adds an AxonType to this NeuronType.
   *
   *  @param type name the type of the Axon.
   *
   *  @return the created AxonType.
   *  @exception ReadOnlyException if the NeuronType was not editable.
   *  @exception NeuronException if the AxonType could not be created
   *  (typically, the type was not unique).
   */
  AxonType   addAxonType(String type)
    throws NeuronException, ReadOnlyException;

  /** Removes the given AxonType.
   *
   *  @param type the type name of the AxonType to remove.
   *  @exception ReadOnlyException if the NeuronType was not editable.
   */
  void       removeAxonType(String type) throws ReadOnlyException;
}



