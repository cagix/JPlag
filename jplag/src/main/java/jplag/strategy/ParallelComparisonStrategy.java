package jplag.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import jplag.GreedyStringTiling;
import jplag.JPlagComparison;
import jplag.JPlagResult;
import jplag.Submission;
import jplag.options.JPlagOptions;

public class ParallelComparisonStrategy extends AbstractComparisonStrategy  {

    public ParallelComparisonStrategy(JPlagOptions options, GreedyStringTiling greedyStringTiling) {
        super(options, greedyStringTiling);
    }

    @Override
    public JPlagResult compareSubmissions(Vector<Submission> submissions, Submission baseCodeSubmission) {
        long start = System.currentTimeMillis(); // TODO remove timing
        if (baseCodeSubmission != null) {
            compareSubmissionsToBaseCode(submissions, baseCodeSubmission);
        }

        long timeBeforeStartInMillis = System.currentTimeMillis();

        List<SubmissionTuple> tuples = new ArrayList<>();
        for (int i = 0; i < (submissions.size() - 1); i++) {
            Submission first = submissions.elementAt(i);
            if (first.tokenList != null) {
                for (int j = (i + 1); j < submissions.size(); j++) {
                    Submission second = submissions.elementAt(j);
                    if (second.tokenList != null) {
                        tuples.add(new SubmissionTuple(first, second));
                    }
                }
            }
        }

        List<JPlagComparison> comparisons = Collections.synchronizedList(new ArrayList<>());
        tuples.parallelStream().forEach(it -> {
            JPlagComparison comparison = greedyStringTiling.compare(it.getLeft(), it.getRight());
            System.out.println("Comparing " + it.getLeft().name + "-" + it.getRight().name + ": " + comparison.percent());
            if (baseCodeSubmission != null) {
                comparison.bcMatchesA = baseCodeMatches.get(comparison.firstSubmission.name);
                comparison.bcMatchesB = baseCodeMatches.get(comparison.secondSubmission.name);
            }
            if (isAboveSimilarityThreshold(comparison)) {
                synchronized (comparisons) {
                    comparisons.add(comparison);
                }
            }
        });
        
        long end = System.currentTimeMillis(); // TODO remove timing
        System.out.println("------------"); // TODO remove timing
        System.out.println("Timed: " + (end - start)); // Seq: 113162 // TODO remove timing
        System.out.println("------------"); // TODO remove timing

        long durationInMillis = System.currentTimeMillis() - timeBeforeStartInMillis;
        return new JPlagResult(comparisons, durationInMillis, submissions.size(), options);
    }


}
