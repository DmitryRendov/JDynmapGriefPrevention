package jahan.khan.jdynmapgriefprevention;

public class JMetrics {
	private JDynmapGriefPrevention jdgp;

	private boolean checkoptoutok;

	public JMetrics(JDynmapGriefPrevention plugin) {
		try {
			this.jdgp = plugin;
			Metrics metrics = new Metrics(this.jdgp);
			this.jdgp.setMetrics(metrics);

			if (metrics.isOptOut()) {
				this.checkoptoutok = false;
			} else {
				this.checkoptoutok = true;
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public boolean isOptOutOk() {
		return this.checkoptoutok;
	}

	public void send() {
		try {
			Metrics metrics = this.jdgp.getMetrics();

			Metrics.Graph featEnabledGraph = metrics.createGraph("EnabledFeatures");
			featEnabledGraph.addPlotter(new OnOffPlotter("Total"));
			if (this.jdgp.updconslog) {
				featEnabledGraph.addPlotter(new OnOffPlotter("consolelog"));
			}
			if (this.jdgp.usetwolayers) {
				featEnabledGraph.addPlotter(new OnOffPlotter("usetwolayers"));
			}
			if (this.jdgp.showcountonlayer) {
				featEnabledGraph.addPlotter(new OnOffPlotter("showcountonlayer"));
			}
			if (this.jdgp.use3d) {
				featEnabledGraph.addPlotter(new OnOffPlotter("use3dregions"));
			}
			if (this.jdgp.publicenabled) {
				featEnabledGraph.addPlotter(new OnOffPlotter("publictrusted"));
			}

			if (this.jdgp.allowBracketsTrust) {
				featEnabledGraph.addPlotter(new OnOffPlotter("allowbracketstrust"));
			}
			if (this.jdgp.debug) {
				featEnabledGraph.addPlotter(new OnOffPlotter("debug"));
			}
			if (this.jdgp.updateCheck) {
				featEnabledGraph.addPlotter(new OnOffPlotter("updatecheck"));
			}

			if (this.jdgp.nwsEnabled) {
				featEnabledGraph.addPlotter(new OnOffPlotter("nwsEnabled"));
			}
			if (this.jdgp.uuidserver.booleanValue()) {
				featEnabledGraph.addPlotter(new OnOffPlotter("uuidserver"));
			}
			if (this.jdgp.useDynmap) {
				featEnabledGraph.addPlotter(new OnOffPlotter("useDynmap"));
			}

			Metrics.Graph claimsGraph = metrics.createGraph("ClaimCategories");

			if ((this.jdgp.numOwners >= 0) && (this.jdgp.numOwners <= 1000)) {
				claimsGraph.addPlotter(new OnOffPlotter("1-1000"));
			}
			if ((this.jdgp.numOwners > 1000) && (this.jdgp.numOwners <= 2000)) {
				claimsGraph.addPlotter(new OnOffPlotter("1001-2000"));
			}
			if ((this.jdgp.numOwners > 2000) && (this.jdgp.numOwners <= 3000)) {
				claimsGraph.addPlotter(new OnOffPlotter("2001-3000"));
			}
			if ((this.jdgp.numOwners > 3000) && (this.jdgp.numOwners <= 4000)) {
				claimsGraph.addPlotter(new OnOffPlotter("3001-4000"));
			}
			if ((this.jdgp.numOwners > 4000) && (this.jdgp.numOwners <= 5000)) {
				claimsGraph.addPlotter(new OnOffPlotter("4001-5000"));
			}
			if ((this.jdgp.numOwners > 5000) && (this.jdgp.numOwners <= 6000)) {
				claimsGraph.addPlotter(new OnOffPlotter("5001-6000"));
			}
			if ((this.jdgp.numOwners > 6000) && (this.jdgp.numOwners <= 7000)) {
				claimsGraph.addPlotter(new OnOffPlotter("6001-7000"));
			}
			if ((this.jdgp.numOwners > 7000) && (this.jdgp.numOwners <= 8000)) {
				claimsGraph.addPlotter(new OnOffPlotter("7001-8000"));
			}
			if ((this.jdgp.numOwners > 8000) && (this.jdgp.numOwners <= 9000)) {
				claimsGraph.addPlotter(new OnOffPlotter("8001-9000"));
			}
			if ((this.jdgp.numOwners > 9000) && (this.jdgp.numOwners <= 10000)) {
				claimsGraph.addPlotter(new OnOffPlotter("9001-10000"));
			}
			if ((this.jdgp.numOwners > 10000) && (this.jdgp.numOwners <= 11000)) {
				claimsGraph.addPlotter(new OnOffPlotter("10001-11000"));
			}
			if ((this.jdgp.numOwners > 11000) && (this.jdgp.numOwners <= 12000)) {
				claimsGraph.addPlotter(new OnOffPlotter("11001-12000"));
			}
			if ((this.jdgp.numOwners > 12000) && (this.jdgp.numOwners <= 13000)) {
				claimsGraph.addPlotter(new OnOffPlotter("12001-13000"));
			}
			if ((this.jdgp.numOwners > 13000) && (this.jdgp.numOwners <= 14000)) {
				claimsGraph.addPlotter(new OnOffPlotter("13001-14000"));
			}
			if ((this.jdgp.numOwners > 14000) && (this.jdgp.numOwners <= 15000)) {
				claimsGraph.addPlotter(new OnOffPlotter("14001-15000"));
			}
			if (this.jdgp.numOwners > 15000) {
				claimsGraph.addPlotter(new OnOffPlotter("15000+"));
			}

			Metrics.Graph claimDistGraph = metrics.createGraph("ClaimDistribution");

			claimDistGraph.addPlotter(new Metrics.Plotter("Used Claims") {
				public int getValue() {
					return JMetrics.this.jdgp.countused;
				}
			});
			claimDistGraph.addPlotter(new Metrics.Plotter("Unused Claims") {
				public int getValue() {
					return JMetrics.this.jdgp.countunused;
				}

			});
			Metrics.Graph claimTypesGraph = metrics.createGraph("ClaimTypes");

			claimTypesGraph.addPlotter(new Metrics.Plotter("Admin Claims") {
				public int getValue() {
					return JMetrics.this.jdgp.countadmin;
				}
			});
			claimTypesGraph.addPlotter(new Metrics.Plotter("Owner used Claims") {
				public int getValue() {
					return JMetrics.this.jdgp.countnormal;
				}
			});
			claimTypesGraph.addPlotter(new Metrics.Plotter("Builder used Claims") {
				public int getValue() {
					return JMetrics.this.jdgp.countbuilder;
				}

			});
			Metrics.Graph trustDistGraph = metrics.createGraph("NumbersOfTrusted");

			trustDistGraph.addPlotter(new Metrics.Plotter("Builders") {
				public int getValue() {
					return JMetrics.this.jdgp.numBuilders;
				}
			});
			trustDistGraph.addPlotter(new Metrics.Plotter("Containers") {
				public int getValue() {
					return JMetrics.this.jdgp.numContainers;
				}
			});
			trustDistGraph.addPlotter(new Metrics.Plotter("Accessors") {
				public int getValue() {
					return JMetrics.this.jdgp.numAccessors;
				}
			});
			trustDistGraph.addPlotter(new Metrics.Plotter("Managers") {
				public int getValue() {
					return JMetrics.this.jdgp.numManagers;
				}

			});
			Metrics.Graph localeGraph = metrics.createGraph("Locale Used");
			localeGraph.addPlotter(new OnOffPlotter(this.jdgp.cfglocale));

			Metrics.Graph versionsGraph = metrics.createGraph("PluginVersions");
			versionsGraph.addPlotter(new OnOffPlotter(this.jdgp.versionString));

			metrics.start();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	class OnOffPlotter extends Metrics.Plotter {
		OnOffPlotter(String name) {
			super();
		}

		public int getValue() {
			return 1;
		}
	}
}
