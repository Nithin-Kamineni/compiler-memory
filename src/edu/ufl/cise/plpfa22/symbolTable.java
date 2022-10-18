package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import java.util.*;

public class symbolTable {
    String scopeId;
    int k;
    int test;

    Stack<String> scope_stack = new Stack<>(); //keeps track of scope number;
    List<String> scopeList = new ArrayList<>();

    class Pair{
        String scopeId;
        Declaration tempDec;
        int scopeLevel;

        public Pair(String scopeId, int scopeLevel, Declaration decTemp) {
            this.scopeId = scopeId;
            this.scopeLevel = scopeLevel;
            this.tempDec = decTemp;
        }

        public String getKey() {
            return scopeId;
        }

        public int getScopeLvl() {
            return scopeLevel;
        }

        public Declaration getValue() {
            return tempDec;
        }
    }

    Hashtable<String, ArrayList<Pair>> hash = new Hashtable<String, ArrayList<Pair>>(); // maps identifiers with corresponding scope numbers
    int currentScope;

    public symbolTable() {
        test=0;
        this.scope_stack = new Stack<String>();
        this.hash = new Hashtable<String, ArrayList<Pair>>();
        this.currentScope = 0;
        test=0;
        this.scopeId = UUID.randomUUID().toString();
        test=0;
        scope_stack.push(scopeId);
    }

    public void enterScopeFirst() {
        currentScope++;
        scopeId = UUID.randomUUID().toString();
        test=0;
        scopeList.add(scopeId);
        test=0;
        scope_stack.push(scopeId);
    }

    public void enterScope() {
        currentScope++;
        test=0;
        scopeId = scopeList.get(k++);
        test=0;
        scope_stack.push(scopeId);
    }


    public void closeScope() {
        currentScope--;
        test=0;
        scope_stack.pop();
        test=0;
        scopeId = scope_stack.peek();
    }

    public void addDec(String ident, Declaration dec) throws PLPException {
        if (hash.containsKey(ident)) {
            ArrayList<Pair> l = hash.get(ident);
            for (int i=0;i<l.size();i++){
                if (l.get(i).getKey() == scopeId) {
                    throw new ScopeException("ID is declared twice is the same scope");
                }
            }

            l.add(new Pair(scopeId, currentScope, dec));
            hash.put(ident, l);
        }else {
            ArrayList<Pair> l = new ArrayList<>();
            l.add(new Pair(scopeId, currentScope, dec));
            hash.put(ident, l);
        }
    }

    public Declaration lookup(String ident) throws PLPException{
        ArrayList<Pair> l = hash.get(ident);
        if (l == null) {
            throw new ScopeException("ID is not declared");
        }

        Declaration dec = null;
        for (Pair p: l) {
            for(int i=0;i<scope_stack.size();i++){
//                System.out.println("out:"+ident);
                if(scope_stack.get(i)==p.getKey()){
//                    System.out.println("in:"+ident);
                    dec = p.getValue();
                }
            }
        }
        return dec;
    }

}