/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.interfaces.ChemObjectChangeEvent;

/**
 *  Base class for all chemical objects that maintain a list of Atoms and
 *  ElectronContainers. <p>
 *
 *  Looping over all Bonds in the AtomContainer is typically done like: <pre>
 *  Bond[] bonds = atomContainer.getBonds();
 *  for (int i = 0; i < bonds.length; i++) {
 *      Bond b = bonds[i];
 *  }
 *  </pre>
 *
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class AtomContainer extends ChemObject implements java.io.Serializable, org.openscience.cdk.interfaces.AtomContainer , ChemObjectListener {

	/**
	 *  Number of atoms contained by this object.
	 */
	protected int atomCount;

	/**
	 *  Number of electronContainers contained by this object.
	 */
	protected int electronContainerCount;

	/**
	 *  Amount by which the bond and arom arrays grow when elements are added and
	 *  the arrays are not large enough for that.
	 */
	protected int growArraySize = 10;

	/**
	 *  Internal array of atoms.
	 */
	protected org.openscience.cdk.interfaces.Atom[] atoms;

	/**
	 *  Internal array of bond.
	 */
	protected org.openscience.cdk.interfaces.ElectronContainer[] electronContainers;

	/**
	 * Internal list of atom parities.
	 */
	protected Hashtable atomParities;


	/**
	 *  Constructs an empty AtomContainer.
	 */
	public AtomContainer() {
        this(10, 10);
	}


	/**
	 * Constructs an AtomContainer with a copy of the atoms and electronContainers
	 * of another AtomContainer (A shallow copy, i.e., with the same objects as in
	 * the original AtomContainer).
	 *
	 * @param  container  An AtomContainer to copy the atoms and electronContainers from
	 */
	public AtomContainer(org.openscience.cdk.interfaces.AtomContainer container)
	{
		this();
		this.add(container);
	}


	/**
	 *  Constructs an empty AtomContainer that will contain a certain number of
	 *  atoms and electronContainers. It will set the starting array lengths to the
	 *  defined values, but will not create any Atom or ElectronContainer's.
	 *
	 *@param  atomCount               Number of atoms to be in this container
	 *@param  electronContainerCount  Number of electronContainers to be in this
	 *      container
	 */
	public AtomContainer(int atomCount, int electronContainerCount)
	{
		this.atomCount = 0;
		this.electronContainerCount = 0;
		atoms = new Atom[atomCount];
		electronContainers = new ElectronContainer[electronContainerCount];
        atomParities = new Hashtable((int)(atomCount/2));
	}

    /**
     * Adds an AtomParity to this container. If a parity is already given for the
     * affected Atom, it is overwritten.
     *
     * @param parity The new AtomParity for this container
     * @see   #getAtomParity
     */
    public void addAtomParity(org.openscience.cdk.interfaces.AtomParity parity) {
        atomParities.put(parity.getAtom(), parity);
    }

    /**
     * Returns the atom parity for the given Atom. If no parity is associated
     * with the given Atom, it returns null.
     *
     * @param  atom   Atom for which the parity must be returned
     * @return The AtomParity for the given Atom, or null if that Atom does
     *         not have an associated AtomParity
     * @see    #addAtomParity
     */
    public org.openscience.cdk.interfaces.AtomParity getAtomParity(org.openscience.cdk.interfaces.Atom atom) {
        return (AtomParity)atomParities.get(atom);
    }
    
	/**
	 *  Sets the array of atoms of this AtomContainer.
	 *
	 *@param  atoms  The array of atoms to be assigned to this AtomContainer
	 *@see           #getAtoms
	 */
	public void setAtoms(org.openscience.cdk.interfaces.Atom[] atoms)
	{
		this.atoms = atoms;
		for (int f = 0; f < atoms.length; f++)
		{
			atoms[f].addListener(this);	
		}
		setAtomCount(atoms.length);
		notifyChanged();

	}


	/**
	 *  Sets the array of electronContainers of this AtomContainer.
	 *
	 *@param  electronContainers  The array of electronContainers to be assigned to
	 *      this AtomContainer
	 *@see  #getElectronContainers
	 */
	public void setElectronContainers(org.openscience.cdk.interfaces.ElectronContainer[] electronContainers)
	{
		this.electronContainers = electronContainers;
		for (int f = 0; f < electronContainers.length; f++)
		{
			electronContainers[f].addListener(this);	
		}
		setElectronContainerCount(electronContainers.length);
		notifyChanged();
	}


	/**
	 *  Set the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be set.
	 *@param  atom    The atom to be stored at position <code>number</code>
	 *@see            #getAtomAt
	 */
	public void setAtomAt(int number, org.openscience.cdk.interfaces.Atom atom)
	{
		atom.addListener(this);
		atoms[number] = atom;
		notifyChanged();
	}


	/**
	 *  Get the atom at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the atom to be retrieved.
	 *@return         The atomAt value
	 *@see            #setAtomAt
	 */
	public org.openscience.cdk.interfaces.Atom getAtomAt(int number)
	{
		return atoms[number];
	}


	/**
	 *  Get the bond at position <code>number</code> in [0,..].
	 *
	 *@param  number  The position of the bond to be retrieved.
	 *@return         The bondAt value
	 *@see            #setElectronContainerAt
	 */
	public org.openscience.cdk.interfaces.Bond getBondAt(int number)
	{
		return getBonds()[number];
	}



	/**
	 * Sets the ElectronContainer at position <code>number</code> in [0,..].
	 *
	 * @param  number            The position of the ElectronContainer to be set.
	 * @param  electronContainer The ElectronContainer to be stored at position <code>number</code>
	 * @see                      #getElectronContainerAt
	 */
	public void setElectronContainerAt(int number, org.openscience.cdk.interfaces.ElectronContainer electronContainer)
	{
		electronContainer.addListener(this);
		electronContainers[number] = electronContainer;
		notifyChanged();
	}


	/**
	 * Sets the number of electronContainers in this container.
	 *
	 * @param  electronContainerCount  The number of electronContainers in this
	 *                                 container
	 * @see                            #getElectronContainerCount
	 */
	public void setElectronContainerCount(int electronContainerCount)
	{
		this.electronContainerCount = electronContainerCount;
		notifyChanged();
	}


	/**
	 *  Sets the number of atoms in this container.
	 *
	 *@param  atomCount  The number of atoms in this container
	 *@see               #getAtomCount
	 */
	public void setAtomCount(int atomCount)
	{
		this.atomCount = atomCount;
		notifyChanged();
	}


	/**
	 *  Returns the array of atoms of this AtomContainer.
	 *
	 *@return    The array of atoms of this AtomContainer
	 *@see       #setAtoms
	 */
	public org.openscience.cdk.interfaces.Atom[] getAtoms()
	{
		Atom[] returnAtoms = new Atom[getAtomCount()];
		System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
		return returnAtoms;
	}


	/**
	 *  Returns an AtomEnumeration for looping over all atoms in this container.
	 *
	 *@return    An AtomEnumeration with the atoms in this container
	 *@see       #getAtoms
	 */
	public Enumeration atoms()
	{
		return new AtomEnumeration(this);
	}


	/**
	 *  Returns the array of electronContainers of this AtomContainer.
	 *
	 *@return    The array of electronContainers of this AtomContainer
	 *@see       #setElectronContainers
	 */
	public org.openscience.cdk.interfaces.ElectronContainer[] getElectronContainers()
	{
		org.openscience.cdk.interfaces.ElectronContainer[] returnElectronContainers = new ElectronContainer[getElectronContainerCount()];
		System.arraycopy(this.electronContainers, 0, returnElectronContainers, 0, returnElectronContainers.length);
		return returnElectronContainers;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 */
	public org.openscience.cdk.interfaces.Bond[] getBonds()
	{
		int bondCount = getBondCount();
		org.openscience.cdk.interfaces.Bond[] result = new Bond[bondCount];
		int bondCounter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer electronContainer = getElectronContainerAt(i);
			if (electronContainer instanceof org.openscience.cdk.interfaces.Bond)
			{
				result[bondCounter] = (Bond) electronContainer;
				bondCounter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@return    The array of Bonds of this AtomContainer
	 *@see       #getElectronContainers
	 *@see       #getBonds
	 */
	public LonePair[] getLonePairs()
	{
		int count = getLonePairCount();
		LonePair[] result = new LonePair[count];
		int counter = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer electronContainer = getElectronContainerAt(i);
			if (electronContainer instanceof LonePair)
			{
				result[counter] = (LonePair) electronContainer;
				counter++;
			}
		}
		return result;
	}


	/**
	 *  Returns the array of Bonds of this AtomContainer.
	 *
	 *@param  atom  Description of the Parameter
	 *@return       The array of Bonds of this AtomContainer
	 *@see          #getElectronContainers
	 *@see          #getBonds
	 */
	public LonePair[] getLonePairs(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector lps = new Vector();
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			ElectronContainer electronContainer = getElectronContainerAt(i);
			if ((electronContainer instanceof LonePair) && 
			    (((LonePair) electronContainer).contains((Atom)atom)))
			{
				lps.add(electronContainer);
			}
		}
		LonePair[] result = new LonePair[lps.size()];
		lps.copyInto(result);
		return result;
	}


	/**
	 *  Returns the atom at position 0 in the container.
	 *
	 *@return    The atom at position 0 .
	 */
	public Atom getFirstAtom()
	{
		return (Atom)atoms[0];
	}


	/**
	 *  Returns the atom at the last position in the container.
	 *
	 *@return    The atom at the last position
	 */
	public Atom getLastAtom()
	{
		return (Atom)atoms[getAtomCount() - 1];
	}


	/**
	 *  Returns the position of a given atom in the atoms array. It returns -1 if
	 *  the atom atom does not exist.
	 *
	 *@param  atom  The atom to be sought
	 *@return       The Position of the atom in the atoms array in [0,..].
	 */
	public int getAtomNumber(org.openscience.cdk.interfaces.Atom atom)
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (getAtomAt(f) == atom)
			{
				return f;
			}
		}
		return -1;
	}


	/**
	 *  Returns the position of the bond between two given atoms in the
	 *  electronContainers array. It returns -1 if the bond does not exist.
	 *
	 *@param  atom1  The first atom
	 *@param  atom2  The second atom
	 *@return        The Position of the bond between a1 and a2 in the
	 *               electronContainers array.
	 */
	public int getBondNumber(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2)
	{
		return (getBondNumber(getBond(atom1, atom2)));
	}


	/**
	 *  Returns the position of a given bond in the electronContainers array. It
	 *  returns -1 if the bond does not exist.
	 *
	 *@param  bond  The bond to be sought
	 *@return       The Position of the bond in the electronContainers array in [0,..].
	 */
	public int getBondNumber(org.openscience.cdk.interfaces.Bond bond)
	{
		for (int f = 0; f < getElectronContainerCount(); f++)
		{
			if (getElectronContainerAt(f) == bond)
			{
				return f;
			}
		}
		return -1;
	}


	/**
	 *  Returns the ElectronContainer at position <code>number</code> in the
	 *  container.
	 *
	 *@param  number  The position of the ElectronContainer to be returned.
	 *@return         The ElectronContainer at position <code>number</code>.
	 *@see            #setElectronContainerAt
	 */
	public ElectronContainer getElectronContainerAt(int number)
	{
		return (ElectronContainer)electronContainers[number];
	}


	/**
	 * Returns the bond that connectes the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public Bond getBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom1))
			{
				if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
						((Bond) electronContainers[i]).getConnectedAtom(atom1) == atom2)
				{
					return (Bond) electronContainers[i];
				}
			}
		}
		return null;
	}


	/**
	 *  Returns an array of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The array of <code>Atom</code>s with the size of connected
	 *      atoms
	 */
	public Atom[] getConnectedAtoms(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector atomsVec = getConnectedAtomsVector(atom);
		Atom[] conAtoms = new Atom[atomsVec.size()];
		atomsVec.copyInto(conAtoms);
		return conAtoms;
	}


	/**
	 *  Returns a vector of all atoms connected to the given atom.
	 *
	 *@param  atom  The atom the bond partners are searched of.
	 *@return       The vector with the size of connected atoms
	 */
	public Vector getConnectedAtomsVector(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector atomsVec = new Vector();
		ElectronContainer electronContainer;
		for (int i = 0; i < electronContainerCount; i++)
		{
			electronContainer = (ElectronContainer)electronContainers[i];
			if (electronContainer instanceof org.openscience.cdk.interfaces.Bond && ((Bond) electronContainer).contains(atom))
			{
				atomsVec.addElement(((Bond) electronContainer).getConnectedAtom(atom));
			}
		}
		return atomsVec;
	}


	/**
	 *  Returns an array of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The array with the size of connected atoms
	 */
	public Bond[] getConnectedBonds(org.openscience.cdk.interfaces.Atom atom)
  {
    Vector bondsVec=getConnectedBondsVector(atom);
		Bond[] conBonds = new Bond[bondsVec.size()];
		bondsVec.copyInto(conBonds);
		return conBonds;
	}
  
	/**
	 *  Returns a Vector of all Bonds connected to the given atom.
	 *
	 *@param  atom  The atom the connected bonds are searched of
	 *@return       The vector with the size of connected atoms
	 */
  public Vector getConnectedBondsVector(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector bondsVec = new Vector();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				bondsVec.addElement(electronContainers[i]);
			}
		}
    return(bondsVec);
  }


	/**
	 *  Returns an array of all electronContainers connected to the given atom.
	 *
	 *@param  atom  The atom the connected electronContainers are searched of
	 *@return       The array with the size of connected atoms
	 */
	public ElectronContainer[] getConnectedElectronContainers(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector bondsVec = new Vector();
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom)) {
				bondsVec.addElement(electronContainers[i]);
			} else if (electronContainers[i] instanceof LonePair &&
                    ((LonePair) electronContainers[i]).contains((Atom)atom)) {
				bondsVec.addElement(electronContainers[i]);
			} else if (electronContainers[i] instanceof SingleElectron &&
					((SingleElectron) electronContainers[i]).contains((Atom)atom)) {
				bondsVec.addElement(electronContainers[i]);
			}
		}
		ElectronContainer[] cons = new ElectronContainer[bondsVec.size()];
		bondsVec.copyInto(cons);
		return cons;
	}


	/**
	 *  Returns the number of connected atoms (degree) to the given atom.
	 *
	 *@param  atomnumber  The atomnumber the degree is searched for
	 *@return             The number of connected atoms (degree)
	 */
	public int getBondCount(int atomnumber)
	{
		return getBondCount(getAtomAt(atomnumber));
	}


	/**
	 *  Returns the number of Atoms in this Container.
	 *
	 *@return    The number of Atoms in this Container
	 *@see       #setAtomCount
	 */
	public int getAtomCount()
	{
		return this.atomCount;
	}


	/**
	 * Returns the number of ElectronContainers in this Container.
	 *
	 * @return    The number of ElectronContainers in this Container
     * @see       #setElectronContainerCount
	 */
	public int getElectronContainerCount()
	{
		return this.electronContainerCount;
	}


	/**
	 *  Returns the number of LonePairs in this Container.
	 *
	 *@return    The number of LonePairs in this Container
	 */
	public int getLonePairCount()
	{
		int count = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof LonePair)
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of Bonds in this Container.
	 *
	 *@return    The number of Bonds in this Container
	 */
	public int getBondCount()
	{
		int bondCount = 0;
		for (int i = 0; i < electronContainerCount; i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond)
			{
				bondCount++;
			}
		}
		return bondCount;
	}


	/**
	 *  Returns the number of Bonds for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of Bonds for this atom
	 */
	public int getBondCount(org.openscience.cdk.interfaces.Atom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count++;
			}
		}
		return count;
	}


	/**
	 *  Returns the number of LonePairs for a given Atom.
	 *
	 *@param  atom  The atom
	 *@return       The number of LonePairs for this atom
	 */
	public int getLonePairCount(org.openscience.cdk.interfaces.Atom atom)
	{
		int count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof LonePair &&
					((LonePair) electronContainers[i]).contains((Atom)atom))
			{
				count++;
			}
		}
		return count;
	}
	/**
	 *  Returns an array of all SingleElectron connected to the given atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public SingleElectron[] getSingleElectron(org.openscience.cdk.interfaces.Atom atom)
	{
		Vector lps = new Vector();
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if ((electronContainers[i] instanceof SingleElectron) && 
				(((SingleElectron) electronContainers[i]).contains((Atom)atom)))
			{
				lps.add(electronContainers[i]);
			}
		}
		SingleElectron[] result = new SingleElectron[lps.size()];
		lps.copyInto(result);
		return result;
	}
	/**
	 *  Returns the sum of the SingleElectron for a given Atom.
	 *
	 *@param  atom  The atom on which the single electron is located
	 *@return       The array of SingleElectron of this AtomContainer
	 */
	public int getSingleElectronSum(org.openscience.cdk.interfaces.Atom atom)
	{
		int count = 0;
		for (int i = 0; i <  getElectronContainerCount(); i++)
		{if ((electronContainers[i] instanceof SingleElectron) && 
			 (((SingleElectron) electronContainers[i]).contains((Atom)atom)))
			{
				count++;
			}
		}
		return count;
	}
	/**
	 * Returns the sum of the bond orders for a given Atom.
	 *
	 * @param  atom  The atom
	 * @return       The number of bondorders for this atom
	 */
	public double getBondOrderSum(org.openscience.cdk.interfaces.Atom atom)
	{
		double count = 0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom))
			{
				count += ((Bond) electronContainers[i]).getOrder();
			}
		}
		return count;
	}

    /**
	 * Returns the maximum bond order that this atom currently has in the context
	 * of this AtomContainer.
	 *
	 * @param  atom  The atom
	 * @return       The maximum bond order that this atom currently has
	 */
	public double getMaximumBondOrder(org.openscience.cdk.interfaces.Atom atom) {
		double max = 0.0;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() > max)
			{
				max = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return max;
	}


	/**
	 *  Returns the minimum bond order that this atom currently has in the context
	 *  of this AtomContainer.
	 *
	 *@param  atom  The atom
	 *@return       The minimim bond order that this atom currently has
	 */
	public double getMinimumBondOrder(org.openscience.cdk.interfaces.Atom atom)
	{
		double min = 6;
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom) &&
					((Bond) electronContainers[i]).getOrder() < min)
			{
				min = ((Bond) electronContainers[i]).getOrder();
			}
		}
		return min;
	}



	/**
	 * Compares this AtomContainer with another given AtomContainer and returns
	 * the Intersection between them. <p>
	 * 
	 * <b>Important Note</b> : This is not the maximum common substructure.
	 *
	 * @param  container  an AtomContainer object
	 * @return            An AtomContainer containing the Intersection between this
	 *                    AtomContainer and another given one
	 */

	public AtomContainer getIntersection(org.openscience.cdk.interfaces.AtomContainer container)
	{
		AtomContainer intersection = new org.openscience.cdk.AtomContainer();

		for (int i = 0; i < getAtomCount(); i++)
		{
			if (container.contains(getAtomAt(i)))
			{
				intersection.addAtom(getAtomAt(i));
			}
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (container.contains(getElectronContainerAt(i)))
			{
				intersection.addElectronContainer(getElectronContainerAt(i));
			}
		}
		return intersection;
	}

	/**
	 *  Adds the <code>ElectronContainer</code>s found in atomContainer to this
	 *  container.
	 *
	 *@param  atomContainer  AtomContainer with the new ElectronContainers
	 */
	public void addElectronContainers(org.openscience.cdk.interfaces.AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}
		notifyChanged();
	}


	/**
	 *  Adds all atoms and electronContainers of a given atomcontainer to this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be added
	 */
	public void add(org.openscience.cdk.interfaces.AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			if (!contains(atomContainer.getAtomAt(f)))
			{
				addAtom(atomContainer.getAtomAt(f));
			}
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			if (!contains(atomContainer.getElectronContainerAt(f)))
			{
				addElectronContainer(atomContainer.getElectronContainerAt(f));
			}
		}
		notifyChanged();
	}


	/**
	 *  Adds an atom to this container.
	 *
	 *@param  atom  The atom to be added to this container
	 */
	public void addAtom(org.openscience.cdk.interfaces.Atom atom)
	{
		if (contains(atom))
		{
			return;
		}

		if (atomCount + 1 >= atoms.length)
		{
			growAtomArray();
		}
		atom.addListener(this);
		atoms[atomCount] = atom;
		atomCount++;
		notifyChanged();
	}


	/**
	 *  Wrapper method for adding Bonds to this AtomContainer.
	 *
	 *@param  bond  The bond to added to this container
	 */
	public void addBond(org.openscience.cdk.interfaces.Bond bond)
	{
		addElectronContainer(bond);
		notifyChanged();
	}


	/**
	 *  Adds a ElectronContainer to this AtomContainer.
	 *
	 *@param  electronContainer  The ElectronContainer to added to this container
	 */
	public void addElectronContainer(org.openscience.cdk.interfaces.ElectronContainer electronContainer)
	{
		if (electronContainerCount + 1 >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		// are we supposed to check if the atoms forming this bond are
		// already in here and add them if neccessary? No, core classes
		// must not check parameter input.
		electronContainer.addListener(this);
		electronContainers[electronContainerCount] = electronContainer;
		electronContainerCount++;
		notifyChanged();
	}


	/**
	 *  Removes all atoms and electronContainers of a given atomcontainer from this
	 *  container.
	 *
	 *@param  atomContainer  The atomcontainer to be removed
	 */
	public void remove(org.openscience.cdk.interfaces.AtomContainer atomContainer)
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			removeAtom(atomContainer.getAtomAt(f));
		}
		for (int f = 0; f < atomContainer.getElectronContainerCount(); f++)
		{
			removeElectronContainer(atomContainer.getElectronContainerAt(f));
		}
		notifyChanged();
	}


	/**
	 * Removes the bond at the given position from this container.
	 *
	 * @param  position  The position of the bond in the electronContainers array
	 * @return           Bond that was removed
	 */
	public ElectronContainer removeElectronContainer(int position)
	{
		ElectronContainer electronContainer = getElectronContainerAt(position);
		electronContainer.removeListener(this);
		for (int i = position; i < electronContainerCount - 1; i++)
		{
			electronContainers[i] = electronContainers[i + 1];
		}
		electronContainers[electronContainerCount - 1] = null;
		electronContainerCount--;
		notifyChanged();
		return electronContainer;
	}


	/**
	 * Removes this ElectronContainer from this container.
	 *
	 * @param  electronContainer    The electronContainer to be removed
	 * @return                      Bond that was removed
	 */
	public ElectronContainer removeElectronContainer(org.openscience.cdk.interfaces.ElectronContainer electronContainer)
	{
		for (int i = getElectronContainerCount() - 1; i >= 0; i--)
		{
			if (electronContainers[i].equals(electronContainer))
			{
				/* we don't notifyChanged here because the
				   method called below does is already  */ 
				return removeElectronContainer(i);
			}
		}
		return null;
	}


	/**
	 * Removes the bond that connects the two given atoms.
	 *
	 * @param  atom1  The first atom
	 * @param  atom2  The second atom
	 * @return        The bond that connectes the two atoms
	 */
	public Bond removeBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainers[i] instanceof org.openscience.cdk.interfaces.Bond &&
					((Bond) electronContainers[i]).contains(atom1))
			{
				if (((Bond) electronContainers[i]).getConnectedAtom(atom1) == atom2)
				{
					/* We don't call notify changed here because
					   the method called below does it */
					return (Bond) removeElectronContainer(electronContainers[i]);
				}
			}
		}
		return null;
	}



	/**
	 *  Removes the atom at the given position from the AtomContainer. Note that
	 *  the electronContainers are unaffected: you also have to take care of
	 *  removing all electronContainers to this atom from the container manually.
	 *
	 *@param  position  The position of the atom to be removed.
	 */
	public void removeAtom(int position)
	{
		atoms[position].removeListener(this);
		for (int i = position; i < atomCount - 1; i++)
		{
			atoms[i] = atoms[i + 1];
		}
		atoms[atomCount - 1] = null;
		atomCount--;
		notifyChanged();
	}


	/**
	 *  Removes the given atom and all connected electronContainers from the
	 *  AtomContainer.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtomAndConnectedElectronContainers(org.openscience.cdk.interfaces.Atom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			ElectronContainer[] electronContainers = getConnectedElectronContainers(atom);
			for (int f = 0; f < electronContainers.length; f++)
			{
				removeElectronContainer(electronContainers[f]);
			}
			removeAtom(position);
		}
		notifyChanged();
	}


	/**
	 *  Removes the given atom from the AtomContainer. Note that the
	 *  electronContainers are unaffected: you also have to take care of removeing
	 *  all electronContainers to this atom from the container.
	 *
	 *@param  atom  The atom to be removed
	 */
	public void removeAtom(org.openscience.cdk.interfaces.Atom atom)
	{
		int position = getAtomNumber(atom);
		if (position != -1)
		{
			removeAtom(position);
		}
		notifyChanged();
	}


	/**
	 * Removes all atoms and bond from this container.
	 */
	public void removeAllElements() {
        for (int f = 0; f < getAtomCount(); f++) {
			getAtomAt(f).removeListener(this);	
		}
		for (int f = 0; f < getElectronContainerCount(); f++) {
			getElectronContainerAt(f).removeListener(this);	
		}
		atoms = new Atom[growArraySize];
		electronContainers = new ElectronContainer[growArraySize];
		atomCount = 0;
		electronContainerCount = 0;
		notifyChanged();
	}


	/**
	 *  Removes electronContainers from this container.
	 */
	public void removeAllElectronContainers()
	{
		for (int f = 0; f < getElectronContainerCount(); f++) {
			getElectronContainerAt(f).removeListener(this);	
		}
		electronContainers = new ElectronContainer[growArraySize];
		electronContainerCount = 0;
		notifyChanged();
	}

    /**
     *  Removes all Bonds from this container.
     */
    public void removeAllBonds() {
    	org.openscience.cdk.interfaces.Bond[] bonds = getBonds();
        for (int i=0; i<bonds.length; i++) {
            removeElectronContainer(bonds[i]);
        }
	notifyChanged();
    }

	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1   Id of the first atom of the Bond in [0,..]
	 *@param  atom2   Id of the second atom of the Bond in [0,..]
	 *@param  order   Bondorder
	 *@param  stereo  Stereochemical orientation
	 */
	public void addBond(int atom1, int atom2, double order, int stereo)
	{
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order, stereo);

		if (contains(bond))
		{
			return;
		}

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
		/* no notifyChanged() here because addBond(bond) does 
		   it already */
	}


	/**
	 *  Adds a bond to this container.
	 *
	 *@param  atom1  Id of the first atom of the Bond in [0,..]
	 *@param  atom2  Id of the second atom of the Bond in [0,..]
	 *@param  order  Bondorder
	 */
	public void addBond(int atom1, int atom2, double order)
	{
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order);

		if (electronContainerCount >= electronContainers.length)
		{
			growElectronContainerArray();
		}
		addBond(bond);
		/* no notifyChanged() here because addBond(bond) does 
		   it already */
	}


	/**
	 *  Adds a LonePair to this Atom.
	 *
	 *@param  atomID  The atom number to which the LonePair is added in [0,..]
	 */
	public void addLonePair(int atomID)
	{
		ElectronContainer lonePair = new LonePair((Atom)atoms[atomID]);
		lonePair.addListener(this);
		addElectronContainer(lonePair);
		/* no notifyChanged() here because addElectronContainer() does 
		   it already */
	}


	/**
	 *  True, if the AtomContainer contains the given ElectronContainer object.
	 *
	 *@param  electronContainer ElectronContainer that is searched for
	 *@return                   True, if the AtomContainer contains the given bond object
	 */
	public boolean contains(org.openscience.cdk.interfaces.ElectronContainer electronContainer)
	{
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			if (electronContainer == electronContainers[i])
			{
				return true;
			}
		}
		return false;
	}


	/**
	 *  True, if the AtomContainer contains the given atom object.
	 *
	 *@param  atom  the atom this AtomContainer is searched for
	 *@return       True, if the AtomContainer contains the given atom object
	 */
	public boolean contains(org.openscience.cdk.interfaces.Atom atom)
	{
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atom == atoms[i])
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  Returns a one line string representation of this Container. This method is
	 *  conform RFC #9.
	 *
	 *@return    The string representation of this Container
	 */
	public String toString()
	{
		ElectronContainer electronContainer;
		StringBuffer stringContent = new StringBuffer();
		stringContent.append("AtomContainer(");
		stringContent.append(this.hashCode()).append(", ");
		stringContent.append("#A:").append(getAtomCount()).append(", ");
		stringContent.append("#EC:").append(getElectronContainerCount()).append(", ");
		for (int i = 0; i < getAtomCount(); i++)
		{
			stringContent.append(getAtomAt(i).toString()).append(", ");
		}
		for (int i = 0; i < getElectronContainerCount(); i++)
		{
			electronContainer = getElectronContainerAt(i);
			// this check should be removed!
			if (electronContainer != null)
			{
				stringContent.append(electronContainer.toString()).append(", ");
			}
		}
        stringContent.append(", AP:[#").append(atomParities.size()).append(", ");
        Enumeration parities = atomParities.elements();
        while (parities.hasMoreElements()) {
			stringContent.append(((AtomParity)parities.nextElement()).toString());
            if (parities.hasMoreElements()) stringContent.append(", ");
		}
		stringContent.append("])");
		return stringContent.toString();
	}


	/**
	 * Clones this AtomContainer object and its content.
	 *
	 * @return    The cloned object
	 * @see       #shallowCopy
	 */
	public Object clone() {
		AtomContainer clone = null;
		ElectronContainer electronContainer = null;
		ElectronContainer newEC = null;
		org.openscience.cdk.interfaces.Atom[] natoms;
		org.openscience.cdk.interfaces.Atom[] newAtoms;
		try {
			clone = (AtomContainer) super.clone();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
        // start from scratch
		clone.removeAllElements();
        // clone all atoms
		for (int f = 0; f < getAtomCount(); f++) {
			clone.addAtom((Atom) getAtomAt(f).clone());
		}
        // clone the electronContainer
		for (int f = 0; f < getElectronContainerCount(); f++) {
			electronContainer = this.getElectronContainerAt(f);
			newEC = new ElectronContainer();
			if (electronContainer instanceof org.openscience.cdk.interfaces.Bond) {
				Bond bond = (Bond) electronContainer;
				newEC = (ElectronContainer)bond.clone();
				natoms = bond.getAtoms();
				newAtoms = new Atom[natoms.length];
				for (int g = 0; g < natoms.length; g++) {
					try {
						newAtoms[g] = clone.getAtomAt(getAtomNumber(natoms[g]));
					} catch (Exception exc) {
						System.out.println("natoms[g]: " + natoms[g]);
						exc.printStackTrace();
					}
				}
				((Bond) newEC).setAtoms(newAtoms);
			} else if (electronContainer instanceof LonePair) {
				Atom atom = ((LonePair) electronContainer).getAtom();
				newEC = (LonePair)electronContainer.clone();
				((LonePair) newEC).setAtom(clone.getAtomAt(getAtomNumber(atom)));
            } else if (electronContainer instanceof SingleElectron) {
                Atom atom = ((SingleElectron) electronContainer).getAtom();
                newEC = (SingleElectron)electronContainer.clone();
                ((SingleElectron) newEC).setAtom(clone.getAtomAt(getAtomNumber(atom)));
			} else {
				//System.out.println("Expecting EC, got: " + electronContainer.getClass().getName());
				newEC = (ElectronContainer) electronContainer.clone();
			}
			clone.addElectronContainer(newEC);
		}
		return clone;
	}

	/**
	 *  Grows the ElectronContainer array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growElectronContainerArray()
	{
		growArraySize = electronContainers.length;
		ElectronContainer[] newelectronContainers = new ElectronContainer[electronContainers.length + growArraySize];
		System.arraycopy(electronContainers, 0, newelectronContainers, 0, electronContainers.length);
		electronContainers = newelectronContainers;
	}


	/**
	 *  Grows the atom array by a given size.
	 *
	 *@see    #growArraySize
	 */
	protected void growAtomArray()
	{
		growArraySize = atoms.length;
		Atom[] newatoms = new Atom[atoms.length + growArraySize];
		System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
		atoms = newatoms;
	}
	
	 /**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(ChemObjectChangeEvent event)
	{
		notifyChanged(event);
	}   

}

