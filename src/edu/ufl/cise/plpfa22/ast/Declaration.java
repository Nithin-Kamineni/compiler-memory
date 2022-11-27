/**  This code is provided for solely for use of students in the course COP5556 Programming Language Principles at the 
 * University of Florida during the Fall Semester 2022 as part of the course project.  No other use is authorized. 
 */

package edu.ufl.cise.plpfa22.ast;

import edu.ufl.cise.plpfa22.IToken;
import edu.ufl.cise.plpfa22.ast.Types.Type;

public abstract class Declaration extends ASTNode {

	public Declaration(IToken firstToken) {
		super(firstToken);
	}

	Type type;
	public Type getType() {
		return type;
	}

	public String getDescriptor() {
		if(type==Type.BOOLEAN){
			return "Z";
		}
		else if(type==Type.NUMBER){
			return "I";
		}
		else{
			return "Ljava/lang/String;";
		}
	}

	public void setType(Type type) {
		this.type = type;
	}



	int nest;
	public int getNest(){return nest;}
	public void setNest(int nest) { this.nest = nest; }

	public String procpath;
	public String  getProcpath(){return procpath;}
	public void setProcpath(String procpath) { this.procpath = procpath; }
}
