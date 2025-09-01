package org.yxw.scan;

import org.yxw.annotation.ComponentScan;
import org.yxw.annotation.Import;
import org.yxw.imported.LocalDateConfiguration;
import org.yxw.imported.ZonedDateConfiguration;

@ComponentScan
@Import({ LocalDateConfiguration.class, ZonedDateConfiguration.class })
public class ScanApplication {

}