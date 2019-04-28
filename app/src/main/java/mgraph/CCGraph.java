package mgraph;

import java.util.HashMap;
import java.util.Scanner;

class CCGraph {
    static final int MAXV = 300;
    static final int MAXDEGREE = 100;
    public int edges[][] = new int[MAXV + 1][MAXDEGREE];
    public int degree[] = new int[MAXV + 1];
    public int nvertices;
    public int nedges;

    //---------Custom------------------
    public String mCharToPos = null;

    CCGraph() {
        nvertices = nedges = 0;
        mCharToPos = "";
        for (int i = 1; i <= MAXV; i++) {

            degree[i] = 0;
        }
    }

    void read_CCGraph(boolean directed) {
        int x, y;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of Guests: ");
        nvertices = sc.nextInt() - 1;
        System.out.println("Enter Guest List as one string like 'abcdefasd' :");
        mCharToPos = sc.next();
        System.out.println("Enter Lookup table size : ");
        int m = sc.nextInt();
        System.out.println("Enter the Connection : <from> <to> ex 'a b' ");
        for (int i = 1; i <= m; i++) {
            String from = sc.next();
            String to = sc.next();
            System.out.printf("[ %s ] -> [%s] \n", from, to);
            x = mCharToPos.indexOf(from);
            y = mCharToPos.indexOf(to);
            insert_edge(x, y, directed);
        }
        sc.close();
    }

    void insert_edge(int x, int y, boolean directed) {
        if (degree[x] > MAXDEGREE)
            System.out.printf(
                    "Warning: insertion (%d, %d) exceeds max degree\n", x, y);
        edges[x][degree[x]] = y;
        degree[x]++;
        if (!directed)
            insert_edge(y, x, true);
        else
            nedges++;
    }

    void print_CCGraph() {
        for (int i = 1; i <= nvertices; i++) {
            System.out.printf("%d: ", i);
            for (int j = degree[i] - 1; j >= 0; j--)
                System.out.printf(" %d", edges[i][j]);
            System.out.printf("\n");
        }
    }
}