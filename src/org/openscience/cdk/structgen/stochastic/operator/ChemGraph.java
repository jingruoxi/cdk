package org.openscience.cdk.structgen.stochastic.operator;

import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.math.RandomNumbersTool;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;

public class ChemGraph
{
	/*Number of atoms in this structure*/
	protected int dim;
	/*Number of atoms needed to form subgraph*/
	protected int numAtoms;
	protected double[][] contab;
	/*Number of atoms that have been traversed */
	protected int travIndex;
	/*Flag: true if atom visited during a traversal*/
	protected boolean[] visited;
	/*Depth first traversal of the graph*/
	protected Vector subGraph;
		
	public ChemGraph(AtomContainer chrom)
	{
		dim = chrom.getAtomCount();
		numAtoms = (int)(dim/2);
		contab = new double[dim][dim];
		contab = ConnectionMatrix.getMatrix(chrom);
	}
	
	public Vector pickDFgraph()
	{
		//depth first search from a randomly selected atom
		
		travIndex = 0;
		subGraph = new Vector();		
		visited = new boolean[dim];			 		
		for (int atom = 0; atom < dim; atom++)	visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0,dim-1);
		recursiveDFT(seedAtom);
	
		return subGraph;
	}

	private void recursiveDFT(int atom)
	{
		if ((travIndex < numAtoms)&&(!visited[atom]))
		{
			subGraph.add(new Integer(atom));
			travIndex++;
			visited[atom] = true;
			
//			for (int nextAtom = 0; nextAtom < dim; nextAtom++) //not generalized
//				if (contab[atom][nextAtom] != 0) recursiveDFT(nextAtom);
            Vector adjSet = new Vector();
            for (int nextAtom = 0; nextAtom < dim; nextAtom++)
            {
				if ((int)contab[atom][nextAtom] != 0)
				{
					adjSet.add(new Integer(nextAtom));
				}
            }
			while (adjSet.size() > 0)
			{
				int adjIndex = RandomNumbersTool.randomInt(0,adjSet.size()-1);
				recursiveDFT(((Integer)adjSet.get(adjIndex)).intValue());
				adjSet.removeElementAt(adjIndex);
			}
			
		}
	}
	
	public Vector pickBFgraph()
	{
		//breadth first search from a randomly selected atom
		
		travIndex = 0;
		subGraph = new Vector();		
		visited = new boolean[dim];			 		
		for (int atom = 0; atom < dim; atom++)	visited[atom] = false;
        int seedAtom = RandomNumbersTool.randomInt(0,dim-1);
		
		Vector atomQueue = new Vector();
		atomQueue.add(new Integer(seedAtom));
		visited[seedAtom] = true;		
		
		while (!atomQueue.isEmpty()&&(subGraph.size()<numAtoms))
		{
			int foreAtom = ((Integer)atomQueue.get(0)).intValue();
			subGraph.add(new Integer(foreAtom));
			atomQueue.removeElementAt(0);
			travIndex++;
			
			Vector adjSet = new Vector();
            for (int nextAtom = 0; nextAtom < dim; nextAtom++)
            {
				if (((int)contab[foreAtom][nextAtom] != 0)&&(!visited[nextAtom]))
				{
					adjSet.add(new Integer(nextAtom));
				}
            }
			while (adjSet.size() > 0)
			{
				int adjIndex = RandomNumbersTool.randomInt(0,adjSet.size()-1);
				atomQueue.add((Integer)adjSet.get(adjIndex));
				visited[((Integer)adjSet.get(adjIndex)).intValue()] = true;
				adjSet.removeElementAt(adjIndex);
			}

		}
		return subGraph;	
	}
	
	public Vector getSubgraph()
	{
		return subGraph;
	}
	
	public void setSubgraph(Vector subgraph)
	{
		subGraph = subgraph;
	}
	
	public int getNumAtoms()
	{
		return numAtoms;
	}
	
	public void setNumAtoms(int numatoms)
	{
		numAtoms = numatoms;
	}
}
