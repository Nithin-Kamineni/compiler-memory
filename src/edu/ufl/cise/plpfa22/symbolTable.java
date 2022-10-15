package edu.ufl.cise.plpfa22;
import edu.ufl.cise.plpfa22.ast.*;

import java.util.*;

public class symbolTable {
    static final boolean doPrint = true;
    private void show(Object input) {
        if (doPrint) {
            System.out.println(input.toString());
        }
    }

    Stack<Integer> scope_stack = new Stack<>(); //keeps track of scope number;
    Hashtable<String, ArrayList<Pair>> hash = new Hashtable<String, ArrayList<Pair>>(); // maps identifiers with corresponding scope numbers
    int currentScope, nextScope;

    public symbolTable() {
        this.scope_stack = new Stack<Integer>();
        this.hash = new Hashtable<String, ArrayList<Pair>>();
        this.currentScope = 0;
        this.nextScope = 1;
        scope_stack.push(currentScope);
    }

    public void enterScope() {
        currentScope = nextScope++;
        scope_stack.push(currentScope);
    }

    public void addDec(String ident, Declaration dec) throws PLPException {
        if (hash.containsKey(ident)) {
            ArrayList<Pair> l = hash.get(ident);
//            System.out.println("here12");
//            System.out.println(l.get(0).getKey());
//            System.out.println(currentScope);
            if(l.get(0).getKey()==currentScope){
                throw new ScopeException("ID is declared twice is the same scope");
            }
            l.add(new Pair(currentScope, dec));
            hash.put(ident, l);
        }else {
            ArrayList<Pair> l = new ArrayList<>();
            l.add(new Pair(currentScope, dec));
            hash.put(ident, l);
        }
    }

    public void closeScope() {
        scope_stack.pop();
        currentScope = scope_stack.peek();
    }

    public Declaration lookup(String ident) throws PLPException{
        ArrayList<Pair> l = hash.get(ident);
        if (l == null) {
            throw new ScopeException("ID is not declared");
//            return null;
        }

        Declaration dec = null;
        int delta = Integer.MAX_VALUE;
        for (Pair p: l) {
            if (p.getKey() <= currentScope && currentScope - p.getKey() <= delta) {
                if (scope_stack.contains(p.getKey())){
                    dec = p.getValue();
                    delta = currentScope - p.getKey();
                }
            }
        }
//        if(dec == null){
//            System.out.println("error");
//        }
        return dec;
    }

    public boolean duplicate(String name) {
        ArrayList<Pair> l = hash.get(name);
        if (l == null) {
            show("l == null");
            return false;
        }

        show("currentScope: " + currentScope);
        for (Pair p: l) {
            show("p.getKey(): " + p.getKey());
            if (p.getKey() == currentScope) {
                return true;
            }
        }
        return false;
    }

    class Pair{
        int i;
        Declaration d;

        public Pair(int i, Declaration d) {
            this.i = i;
            this.d = d;
        }

        public int getKey() {
            return i;
        }

        public Declaration getValue() {
            return d;
        }
    }

}