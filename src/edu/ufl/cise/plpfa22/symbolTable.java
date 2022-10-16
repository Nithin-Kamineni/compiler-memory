package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import java.util.*;

public class symbolTable {
    static final boolean doPrint = true;
    String scopeId;

    private void show(Object input) {
        if (doPrint) {
            System.out.println(input.toString());
        }
    }

    Stack<String> scope_stack = new Stack<>(); //keeps track of scope number;

    Hashtable<String, ArrayList<Pair>> hash = new Hashtable<String, ArrayList<Pair>>(); // maps identifiers with corresponding scope numbers
    int currentScope;

    public symbolTable() {
        this.scope_stack = new Stack<String>();
        this.hash = new Hashtable<String, ArrayList<Pair>>();
        this.currentScope = 0;
        this.scopeId = UUID.randomUUID().toString();
        scope_stack.push(scopeId);
    }

    public void enterScope() {
        currentScope++;
        scopeId = UUID.randomUUID().toString();
        scope_stack.push(scopeId);
    }

    public void closeScope() {
        currentScope--;
        scope_stack.pop();
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

//            scopeId = UUID.randomUUID().toString();
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

//    public boolean duplicate(String name) {
//        ArrayList<Pair> l = hash.get(name);
//        if (l == null) {
//            show("l == null");
//            return false;
//        }
//
//        show("currentScope: " + currentScope);
//        for (Pair p: l) {
//            show("p.getKey(): " + p.getKey());
////            if (p.getKey() == l) {
////                return true;
////            }
//        }
//        return false;
//    }

    class Pair{
        String scopeId;
        Declaration d;
        int scopeLevel;

        public Pair(String scopeId, int scopeLevel, Declaration d) {
            this.scopeId = scopeId;
            this.scopeLevel = scopeLevel;
            this.d = d;
        }

        public String getKey() {
            return scopeId;
        }

        public int getScopeLvl() {
            return scopeLevel;
        }

        public Declaration getValue() {
            return d;
        }
    }

}