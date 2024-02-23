package de.jplag.cli;

import de.jplag.cli.options.CliOptions;
import de.jplag.cli.picocli.CliInputHandler;
import de.jplag.clustering.ClusteringOptions;
import de.jplag.clustering.Preprocessing;
import de.jplag.merging.MergingOptions;
import de.jplag.options.JPlagOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles the building of JPlag options from the cli options
 */
public class JPlagOptionsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JPlagOptionsBuilder.class);

    private final CliInputHandler cliInputHandler;
    private final CliOptions cliOptions;

    /**
     * @param cliInputHandler The cli handler containing the parsed cli options
     */
    public JPlagOptionsBuilder(CliInputHandler cliInputHandler) {
        this.cliInputHandler = cliInputHandler;
        this.cliOptions = this.cliInputHandler.getCliOptions();
    }

    /**
     * Builds the JPlag options
     * @return The JPlag options
     * @throws CliException If the input handler could properly parse everything.
     */
    public JPlagOptions buildOptions() throws CliException {
        Set<File> submissionDirectories = new HashSet<>(List.of(this.cliOptions.rootDirectory));
        Set<File> oldSubmissionDirectories = Set.of(this.cliOptions.oldDirectories);
        List<String> suffixes = List.of(this.cliOptions.advanced.suffixes);
        submissionDirectories.addAll(List.of(this.cliOptions.newDirectories));
        submissionDirectories.addAll(this.cliInputHandler.getSubcommandSubmissionDirectories());


        ClusteringOptions clusteringOptions = getClusteringOptions();
        MergingOptions mergingOptions = getMergingOptions();

        JPlagOptions jPlagOptions = new JPlagOptions(this.cliInputHandler.getSelectedLanguage(), this.cliOptions.minTokenMatch, submissionDirectories,
                oldSubmissionDirectories, null, this.cliOptions.advanced.subdirectory, suffixes, this.cliOptions.advanced.exclusionFileName,
                JPlagOptions.DEFAULT_SIMILARITY_METRIC, this.cliOptions.advanced.similarityThreshold, this.cliOptions.shownComparisons, clusteringOptions,
                this.cliOptions.advanced.debug, mergingOptions, this.cliOptions.normalize);

        String baseCodePath = this.cliOptions.baseCode;
        File baseCodeDirectory = baseCodePath == null ? null : new File(baseCodePath);
        if (baseCodeDirectory == null || baseCodeDirectory.exists()) {
            return jPlagOptions.withBaseCodeSubmissionDirectory(baseCodeDirectory);
        }
        logger.error("Using legacy partial base code API. Please migrate to new full path base code API.");
        return jPlagOptions.withBaseCodeSubmissionName(baseCodePath);
    }

    private ClusteringOptions getClusteringOptions() {
        ClusteringOptions clusteringOptions = new ClusteringOptions().withEnabled(!this.cliOptions.clustering.disable)
                .withAlgorithm(this.cliOptions.clustering.enabled.algorithm).withSimilarityMetric(this.cliOptions.clustering.enabled.metric)
                .withSpectralKernelBandwidth(this.cliOptions.clusterSpectralBandwidth).withSpectralGaussianProcessVariance(this.cliOptions.clusterSpectralNoise)
                .withSpectralMinRuns(this.cliOptions.clusterSpectralMinRuns).withSpectralMaxRuns(this.cliOptions.clusterSpectralMaxRuns)
                .withSpectralMaxKMeansIterationPerRun(this.cliOptions.clusterSpectralKMeansIterations)
                .withAgglomerativeThreshold(this.cliOptions.clusterAgglomerativeThreshold)
                .withAgglomerativeInterClusterSimilarity(this.cliOptions.clusterAgglomerativeInterClusterSimilarity);

        if (this.cliOptions.clusterPreprocessingNone) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.NONE);
        }

        if (this.cliOptions.clusterPreprocessingCdf) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION);
        }

        if (this.cliOptions.clusterPreprocessingPercentile != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.PERCENTILE)
                    .withPreprocessorPercentile(this.cliOptions.clusterPreprocessingPercentile);
        }

        if (this.cliOptions.clusterPreprocessingThreshold != 0) {
            clusteringOptions = clusteringOptions.withPreprocessor(Preprocessing.THRESHOLD)
                    .withPreprocessorThreshold(this.cliOptions.clusterPreprocessingThreshold);
        }

        return clusteringOptions;
    }

    private MergingOptions getMergingOptions() {
        return new MergingOptions(this.cliOptions.merging.enabled, this.cliOptions.merging.minimumNeighborLength, this.cliOptions.merging.maximumGapSize);
    }
}
