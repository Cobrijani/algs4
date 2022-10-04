package week9;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaseballElimination {

    private final Map<String, Integer> indexMap;
    private final TeamStats[] teamStats;
    private final Queue<String>[] cache;

    public BaseballElimination(String filename) {
        try {
            In in = new In(filename);

            int numElements = in.readInt();

            teamStats = new TeamStats[numElements];
            cache = (LinkedList<String>[]) new LinkedList[numElements];
            indexMap = new HashMap<>();

            for (int i = 0; i < numElements; i++) {
                final String name = in.readString();
                final int w = in.readInt();
                final int l = in.readInt();
                final int r = in.readInt();

                final int[] remain = new int[numElements];
                for (int j = 0; j < numElements; j++) {
                    remain[j] = in.readInt();
                }
                teamStats[i] = new TeamStats(name, w, l,
                        r,
                        remain);
                indexMap.put(name, i);
            }
        } catch (ArrayIndexOutOfBoundsException |
                 NullPointerException |
                 NoSuchElementException |
                 IllegalArgumentException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public int numberOfTeams() {
        return teamStats.length;
    }

    public Iterable<String> teams() {
        return Arrays.stream(teamStats).map(TeamStats::getName).collect(Collectors.toList());
    }

    public int wins(String team) {
        checkNotNull(team);
        checkIfExists(team);
        return teamStats[indexMap.get(team)].wins;
    }

    public int losses(String team) {
        checkNotNull(team);
        checkIfExists(team);
        return teamStats[indexMap.get(team)].losses;
    }

    public int remaining(String team) {
        checkNotNull(team);
        checkIfExists(team);
        return teamStats[indexMap.get(team)].remaining;
    }

    public int against(String team1, String team2) {
        checkNotNull(team1);
        checkIfExists(team1);
        checkNotNull(team2);
        checkIfExists(team2);
        return teamStats[indexMap.get(team1)].remainingMatches[indexMap.get(team2)];
    }

    public boolean isEliminated(String team) {
        checkNotNull(team);
        checkIfExists(team);

        int teamIndex = indexMap.get(team);

        if (cache[teamIndex] == null) {
            computeElimination(teamIndex);
        }
        return !cache[teamIndex].isEmpty();
    }

    private Queue<String> computeNonTrivial(int teamIndex) {
        FlowNetwork flowNetwork = constructFlowNetwork(teamIndex);
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);

        Queue<String> retVal = new LinkedList<>();

        int beginCounter = flowNetwork.V() - 1 - (teamStats.length - 1);
        for (int i = 0; i < teamStats.length; i++) {
            if (i == teamIndex) {
                continue;
            }
            if (fordFulkerson.inCut(beginCounter++)) {
                retVal.add(teamStats[i].name);
            }
        }

        return retVal;
    }

    private void computeElimination(int teamIndex) {
        final Queue<String> trivial = computeTrivialElimination(teamIndex);

        if (!trivial.isEmpty()) {
            cache[teamIndex] = trivial;
            return;
        }
        cache[teamIndex] = computeNonTrivial(teamIndex);
    }


    public Iterable<String> certificateOfElimination(String team) {
        checkNotNull(team);
        checkIfExists(team);
        int teamIndex = indexMap.get(team);

        if (cache[teamIndex] == null) {
            computeElimination(teamIndex);
        }
        return cache[teamIndex].isEmpty() ? null : cache[teamIndex];
    }


    private FlowNetwork constructFlowNetwork(int teamIndex) {
        //indexing source 0
        //begin games = 1 -- firstRowVertices - 1
        //second row = firstRowVertices -- secondRowVertices - 1
        //indexing sink length -1;
        int totalMatchesBetweenTeams = teamStats.length - 2;// exclude this team and self

        int firstRowVertices = IntStream.range(1, totalMatchesBetweenTeams + 1).reduce(0, Integer::sum);
        int secondRowVertices = teamStats.length - 1;


        final int numVertices = 1 + firstRowVertices + secondRowVertices + 1;
        final FlowNetwork flowNetwork = new FlowNetwork(numVertices);


        int nodeCounter = 1;
        int outsideCounter = 1;
        for (int i = 0; i < teamStats.length; i++) {
            if (i == teamIndex) {
                continue;
            }

            int thirdCounter = outsideCounter;
            for (int j = i; j < teamStats.length; j++) {
                if (i == j || j == teamIndex) {
                    continue;
                }
                thirdCounter++;
                int firstNode = firstRowVertices + outsideCounter;
                int secondNode = firstRowVertices + thirdCounter;
                flowNetwork.addEdge(new FlowEdge(nodeCounter, firstNode, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(nodeCounter, secondNode, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(0, nodeCounter, teamStats[i].remainingMatches[j]));
                nodeCounter++;
            }
            outsideCounter++;
        }

        int counter = firstRowVertices + 1;
        for (int i = 0; i < teamStats.length; i++) {
            if (i == teamIndex) {
                continue;
            }
            flowNetwork.addEdge(new FlowEdge(counter++, numVertices - 1,
                    teamStats[teamIndex].wins + teamStats[teamIndex].remaining - teamStats[i].wins));
        }

        return flowNetwork;
    }

    private Queue<String> computeTrivialElimination(int teamIndex) {
        TeamStats team = teamStats[teamIndex];
        int possibleWins = team.wins + team.remaining;

        final Queue<String> retVal = new LinkedList<>();

        for (int i = 0; i < teamStats.length; i++) {
            if (i == teamIndex) {
                continue;
            }

            if (possibleWins < teamStats[i].wins) {
                retVal.add(teamStats[i].name);
            }

        }
        return retVal;
    }

    private void checkNotNull(Object param) {
        if (param == null) {
            throw new IllegalArgumentException();
        }
    }

    private void checkIfExists(String team) {
        if (!indexMap.containsKey(team)) {
            throw new IllegalArgumentException();
        }
    }

    private static class TeamStats {

        private final String name;
        private final int wins;
        private final int losses;
        private final int remaining;
        private final int[] remainingMatches;

        public TeamStats(String name, int wins, int losses, int remaining, int[] remainingMatches) {
            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.remaining = remaining;
            this.remainingMatches = remainingMatches;
        }

        public String getName() {
            return name;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public int getRemaining() {
            return remaining;
        }

        public int[] getRemainingMatches() {
            return remainingMatches;
        }

        @Override
        public String toString() {
            return "TeamStats{" +
                    "n='" + name + '\'' +
                    ", w=" + wins +
                    ", l=" + losses +
                    ", r=" + remaining +
                    ", m=" + Arrays.toString(remainingMatches) +
                    '}';
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
