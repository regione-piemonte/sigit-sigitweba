# Project Title
SIGIT-SIGITWEBA

# Project Description
Questa componente è un'applicazione web multiportale (pubblicato su due portali: sisp e rupar), che consente l'accreditamento alla gestione degli impianti termici.

# Configurations
Aprire il file buildfiles/dev-rp-01.properties e configurare i seguenti parametri:
- remincl.resource.provider.sisp: portale reperimento risorse statiche sisp;
- portal.home.sisp: portale di pubblicazione sisp;
- remincl.resource.provider.rupar: portale reperimento risorse statiche rupar;
- portal.home.rupar: portale di pubblicazione rupar;
- shibboleth.logout.url.sisp: url di logout sisp;
- shibboleth.logout.url.rupar: url di logout rupar;
- tavt.tavt.tavt.pa.url: servizi di fruizione Tavole Trasversali;
- tope.provider.url: servizi di fruizione Toponomastica regionale;
- mail.host: host del server di posta;
- mail.port: porta del server di posta;

# Getting Started 
Clonare la componente sigit-sigitweba dal repository git 
Modificare i file di configurazione e compilare il progetto.

# Prerequisites
I prerequisiti per l'installazione della componente sono i seguenti:
## Software
- [JDK 8](https://www.apache.org)
- [Apache 2.4](https://www.apache.org)
- [RedHat JBoss 6.4 GA](https://developers.redhat.com)  
- [PostgreSQL 9.6.11](https://www.postgresql.org/download/)  

- Tutte le librerie elencate nel BOM.csv devono essere accessibili per compilare il progetto. Le librerie sono pubblicate su http://repart.csi.it, ma per semplicità sono state incluse nella cartella lib/, ad esclusione della libreria weblogic-client-3.0.0.jar che deve essere scaricata autonomamente dal sito di Oracle.

# Installing
Installare il file ear sull'ambiente JBoss 6.4. 

# Versioning
Per la gestione del codice sorgente viene utilizzata Git. Per la gestione del versioning si fa riferimento alla metodologia [Semantic Versioning](https://semver.org/).

# Copyrights
(C) Copyright 2021 Regione Piemonte

# License
Questo software è distribuito con licenza EUPL-1.2
Consultare i file EUPL v1_2 IT-LICENSE.txt e EUPL v1_2 EN-LICENSE.txt per maggiori dettagli.

