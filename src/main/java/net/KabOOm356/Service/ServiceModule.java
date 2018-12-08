/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Service;

import net.KabOOm356.Service.ConfigurationService;
import net.KabOOm356.Service.LastViewedReportService;
import net.KabOOm356.Service.PermissionService;
import net.KabOOm356.Service.PlayerMessageService;
import net.KabOOm356.Service.PlayerService;
import net.KabOOm356.Service.ReportCountService;
import net.KabOOm356.Service.ReportInformationService;
import net.KabOOm356.Service.ReportLimitService;
import net.KabOOm356.Service.ReportPermissionService;
import net.KabOOm356.Service.ReportValidatorService;
import net.KabOOm356.Service.SQLStatServices.ModeratorStatService;
import net.KabOOm356.Service.SQLStatServices.PlayerStatService;
import net.KabOOm356.Service.Store.StoreModule;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceModule {
    private static final Logger log = LogManager.getLogger(ServiceModule.class);
    private final StoreModule storeModule;
    private final PlayerMessageService playerMessageService;
    private final PlayerService playerService;
    private final LastViewedReportService lastViewedReportService;
    private final ReportCountService reportCountService;
    private final ReportValidatorService reportValidatorService;
    private final ReportInformationService reportInformationService;
    private final ReportPermissionService reportPermissionService;
    private final PermissionService permissionService;
    private final ReportLimitService reportLimitService;
    private final ModeratorStatService modStatsService;
    private final PlayerStatService playerStatsService;
    private final ConfigurationService configurationService;

    public ServiceModule(StoreModule storeModule) {
        if (log.isDebugEnabled()) {
            log.info("Initializing services...");
        }
        Validate.notNull((Object)storeModule);
        this.storeModule = storeModule;
        this.permissionService = new PermissionService(this);
        this.playerService = new PlayerService(this);
        this.playerMessageService = new PlayerMessageService(this);
        this.lastViewedReportService = new LastViewedReportService(this);
        this.reportCountService = new ReportCountService(this);
        this.reportValidatorService = new ReportValidatorService(this);
        this.reportInformationService = new ReportInformationService(this);
        this.reportPermissionService = new ReportPermissionService(this);
        this.configurationService = new ConfigurationService(this);
        this.reportLimitService = new ReportLimitService(this);
        this.modStatsService = new ModeratorStatService(this);
        this.playerStatsService = new PlayerStatService(this);
    }

    public ReportPermissionService getReportPermissionService() {
        return this.reportPermissionService;
    }

    public PlayerService getPlayerService() {
        return this.playerService;
    }

    public LastViewedReportService getLastViewedReportService() {
        return this.lastViewedReportService;
    }

    public ReportCountService getReportCountService() {
        return this.reportCountService;
    }

    public ReportValidatorService getReportValidatorService() {
        return this.reportValidatorService;
    }

    public ReportInformationService getReportInformationService() {
        return this.reportInformationService;
    }

    protected StoreModule getStore() {
        return this.storeModule;
    }

    public PermissionService getPermissionService() {
        return this.permissionService;
    }

    public PlayerMessageService getPlayerMessageService() {
        return this.playerMessageService;
    }

    public ReportLimitService getReportLimitService() {
        return this.reportLimitService;
    }

    public ModeratorStatService getModStatsService() {
        return this.modStatsService;
    }

    public PlayerStatService getPlayerStatsService() {
        return this.playerStatsService;
    }

    public ConfigurationService getConfigurationService() {
        return this.configurationService;
    }
}

