package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IDependency;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IProject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Dependency;
import rx.Observable;
import rx.Single;

import java.util.Arrays;
import java.util.regex.Pattern;

public class DependencyDomain {
    private static final Logger logger = LoggerFactory.getLogger(DependencyDomain.class);


    private final IDependency dependencyDao;

    public DependencyDomain(IDependency dependencyDao) {
        this.dependencyDao = dependencyDao;
    }

    public Single<Boolean> create(String resource, String usedBY) {
        return dependencyDao.create(resource, usedBY);
    }

    public Single<Integer> removeAll() {
        return dependencyDao.removeAll();
    }


    public Observable<String> findAllResource() {
        return dependencyDao.findAllResource();
    }

    public Observable<String> usedBy(String resource) {
        return dependencyDao.usedBy(resource) ;
    }


    public Observable<Dependency> findAll() {
        return dependencyDao.findAll() ;
    }

    public Observable<String> save(String artifactId, IProject iProject) {
        String usedBy = new DependenciesSanitizer(artifactId).run();
        return dependencyDao.removeByUsedBy(usedBy)
                .doOnSuccess((nb) -> logger.debug("Delete : " + nb))
                .flatMap(nb -> iProject.readByName(artifactId))
                .toObservable()
                .flatMap(project -> Observable.from(project.javaDeps()))
                .map(str -> new DependenciesSanitizer(str).run())
                .filter(resource -> !"STARTER".equals(resource))
                .distinct()
                .flatMap(resource -> dependencyDao.create(resource, usedBy)
                        .map(res -> " resource '" + resource +
                                "' used by '" + usedBy +
                                "' : " + res)
                        .toObservable()
                );
    }



    public static class DependenciesSanitizer {

        private static final Pattern[] PATTERNS = new Pattern[]{
                Pattern.compile("-MANAGER"),
                Pattern.compile("-SERVICE"),
                Pattern.compile("-IMPL"),
                Pattern.compile("-CLIENT"),
        };
        private final String str;

        DependenciesSanitizer(String str) {
            this.str = str;
        }

        private String innerRun(String chaine, Pattern[] regexps) {
            if (regexps.length == 0) {
                return chaine;
            }
            String newChaine = regexps[0].matcher(chaine).replaceAll("");
            Pattern[] newPatterns = (regexps.length == 1) ? new Pattern[0] : Arrays.copyOfRange(regexps, 1, regexps.length);
            return innerRun(newChaine, newPatterns);

        }

        public String run() {
            String US = str.toUpperCase();
            return innerRun(US, PATTERNS);
        }
    }
}
