package org.testcontainers.containers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

public class CrateDBContainer<SELF extends CrateDBContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    public static final String NAME = "cratedb";

    public static final String IMAGE = "crate";

    public static final String DEFAULT_TAG = "latest";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("crate");

    public static final Integer CRATEDB_PG_PORT = 5432;

    public static final Integer CRATEDB_HTTP_PORT = 4200;

    private String databaseName = "crate";

    private String username = "crate";

    private String password = "crate";

    /**
     * @deprecated use {@link #CrateDBContainer(DockerImageName)} or {@link #CrateDBContainer(String)} instead
     */
    @Deprecated
    public CrateDBContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public CrateDBContainer(final String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public CrateDBContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        this.waitStrategy = Wait.forHttp("/").forPort(CRATEDB_HTTP_PORT).forStatusCode(200);

        addExposedPort(CRATEDB_PG_PORT);
        addExposedPort(CRATEDB_HTTP_PORT);
    }

    /**
     * @return the ports on which to check if the container is ready
     * @deprecated use {@link #getLivenessCheckPortNumbers()} instead
     */
    @NotNull
    @Override
    @Deprecated
    protected Set<Integer> getLivenessCheckPorts() {
        return super.getLivenessCheckPorts();
    }

    @Override
    public String getDriverClassName() {
        return "io.crate.client.jdbc.CrateDriver";
    }

    @Override
    public String getJdbcUrl() {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return (
            "jdbc:crate://" +
            getHost() +
            ":" +
            getMappedPort(CRATEDB_PG_PORT) +
            "/" +
            databaseName +
            additionalUrlParams
        );
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
