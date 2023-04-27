package de.jplag.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.clustering.Preprocessing;

class ClusteringTest extends CommandLineInterfaceTest {

    private static final double EPSILON = 0.000001;

    @Test
    void parseSkipClustering() {
        String argument = "--cluster-skip";
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(false, options.clusteringOptions().enabled());
    }

    @Test
    void parseDefaultClustering() {
        buildOptionsFromCLI(CURRENT_DIRECTORY);
        assertEquals(true, options.clusteringOptions().enabled());
    }

    @Test
    void parsePercentilePreProcessor() {
        String argument = buildArgument("--cluster-pp-percentile", Double.toString(0.5));
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.PERCENTILE, options.clusteringOptions().preprocessor());
        assertEquals(0.5, options.clusteringOptions().preprocessorPercentile(), EPSILON);
    }

    @Test
    void parseCdfPreProcessor() {
        String argument = "--cluster-pp-cdf";
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.CUMULATIVE_DISTRIBUTION_FUNCTION, options.clusteringOptions().preprocessor());
    }

    @Test
    void parseNoPreProcessor() {
        String argument = "--cluster-pp-none";
        buildOptionsFromCLI(argument, CURRENT_DIRECTORY);
        assertEquals(Preprocessing.NONE, options.clusteringOptions().preprocessor());
    }

}
