package org.yxw.io;

import org.yaml.snakeyaml.resolver.Resolver;

/**
 * Disable ALL implicit convert and treat all values as string.
 */
public class NoImplicitResolver extends Resolver {

    public NoImplicitResolver() {
        super();
        super.yamlImplicitResolvers.clear();
    }
}