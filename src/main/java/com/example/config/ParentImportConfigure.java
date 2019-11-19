package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ImportCarConfig.class, ImportHouseConfig.class})
public class ParentImportConfigure {
}
