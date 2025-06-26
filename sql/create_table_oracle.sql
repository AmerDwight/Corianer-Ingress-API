-- MSM Oracle Create Table Script
	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_FAB_FAB (
		FAB_ID VARCHAR2(20) NOT NULL,
		SITE VARCHAR2(40),
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_C_FAB_FAB PRIMARY KEY (FAB_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE msm.c_core_proxy (
	  proxy_id VARCHAR2(20 CHAR) NOT NULL,
	  scheme VARCHAR2(40 CHAR),
	  proxy_host VARCHAR2(40 CHAR) NOT NULL,
	  proxy_port NUMBER(11),
	  authenticate_flag VARCHAR2(1 CHAR) DEFAULT 'N',
	  proxy_account VARCHAR2(20 CHAR),
	  proxy_password VARCHAR2(20 CHAR),
	  active_status VARCHAR2(20 CHAR),
	  CONSTRAINT pk_c_core_proxy PRIMARY KEY (proxy_id)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE msm.c_net_ref (
	  hostname VARCHAR2(40 CHAR) NOT NULL,
	  proxy_ref VARCHAR2(40 CHAR),
	  CONSTRAINT pk_c_net_ref PRIMARY KEY (hostname)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE msm.c_fab_proxy (
	  fab_id VARCHAR2(20 CHAR) NOT NULL,
	  proxy_id VARCHAR2(20 CHAR) NOT NULL,
	  CONSTRAINT pk_c_fab_proxy PRIMARY KEY (fab_id, proxy_id),
	  CONSTRAINT fk_c_fab_proxy_fab FOREIGN KEY (fab_id) REFERENCES msm.c_fab_fab (fab_id),
	  CONSTRAINT fk_c_fab_proxy_proxy FOREIGN KEY (proxy_id) REFERENCES msm.c_core_proxy (proxy_id)
	);



	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_SYS_SYSTEM (
		SYSTEM_ID VARCHAR2(20) NOT NULL,
		SYSTEM_NAME VARCHAR2(40),
		SERVICE_LEVEL NUMBER,
		SYSTEM_DESC VARCHAR2(240),
		OWNER VARCHAR2(40),
		ACTIVE_STATUS VARCHAR2(20) NOT NULL,
		APPLICABLE_FLAG VARCHAR2(1) DEFAULT 'Y'
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CREATE_TIME TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
		CONSTRAINT PK_C_SYS_SYSTEM PRIMARY KEY (SYSTEM_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_SYS_DPY (
		FAB_ID VARCHAR2(20) NOT NULL,
		SYSTEM_ID VARCHAR2(20) NOT NULL,
		ACTIVE_STATUS VARCHAR2(20) NOT NULL,
		CREATE_TIME TIMESTAMP(6),
		PROXY_REQUIRED VARCHAR2(1),
		HEALTH_CHECK_PATH VARCHAR2(100),
		LM_TIME TIMESTAMP(6),
		LM_USER VARCHAR2(40),
		SCHEME VARCHAR2(20) DEFAULT 'http',
		SYSTEM_HOST VARCHAR2(40),
		SYSTEM_PORT NUMBER,
		CONSTRAINT PK_C_SYS_DPY PRIMARY KEY (FAB_ID, SYSTEM_ID),
		CONSTRAINT FK_CFS_C_SYS_SYSTEM FOREIGN KEY (SYSTEM_ID) REFERENCES MSM.C_SYS_SYSTEM(SYSTEM_ID),
		CONSTRAINT FK_CFS_C_FAB_FAB FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_MS_MS (
		MS_ID VARCHAR2(40) NOT NULL,
		ACTIVE_STATUS VARCHAR2(20),
		APPLICABLE_FLAG VARCHAR2(1),
		CREATE_TIME TIMESTAMP(6),
		LM_TIME TIMESTAMP(6),
		LM_USER VARCHAR2(40),
		MS_CATEGORY VARCHAR2(40),
		MS_NAME VARCHAR2(40),
		MS_DESC VARCHAR2(240),
		MS_TYPE VARCHAR2(40),
		OWNER VARCHAR2(40),
		PROCESS_STAGE VARCHAR2(40),
		SYSTEM_ID VARCHAR2(20) NOT NULL,
		CONSTRAINT PK_C_MS_MS PRIMARY KEY (MS_ID),
		CONSTRAINT FK_C_MS_MS_SYSTEM_ID FOREIGN KEY (SYSTEM_ID) REFERENCES MSM.C_SYS_SYSTEM (SYSTEM_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_MS_DOC (
		MS_ID VARCHAR2(20) NOT NULL,
		DOC_SEQUENCE NUMBER(20) NOT NULL,
		DOC_TYPE VARCHAR2(20),
		DOC_SPEC VARCHAR2(20),
		DOC_VERSION VARCHAR2(40),
		DOC_PATCH_DESC VARCHAR2(240),
		DOC_CONTENT CLOB,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_C_MS_DOC PRIMARY KEY (MS_ID, DOC_SEQUENCE),
		CONSTRAINT FK_C_MS_DOC_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_MS_ENDPOINT (
		ENDPOINT_ID VARCHAR2(20) NOT NULL,
		MS_ID VARCHAR2(20) NOT NULL,
		MS_HOST_URI VARCHAR2(100),
		MS_GW_URI VARCHAR2(100),
		INPUT_DEF VARCHAR2(2000),
		OUTPUT_DEF VARCHAR2(2000),
		HTTP_METHOD VARCHAR2(100) DEFAULT 'ALL',
		ACTIVE_STATUS VARCHAR2(20),
		LM_TIME TIMESTAMP(6),
		LM_USER VARCHAR2(40),
		MS_ITF_TYPE VARCHAR2(20),
		CONSTRAINT PK_C_MS_ENDPOINT PRIMARY KEY (ENDPOINT_ID),
		CONSTRAINT FK_C_MS_ENDPOINT_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_MS_DPY (
		MS_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(20) NOT NULL,
		ACTIVE_STATUS VARCHAR2(20) NOT NULL,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CREATE_TIME TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
		CONSTRAINT PK_C_MS_DPY PRIMARY KEY (MS_ID, FAB_ID),
		CONSTRAINT FK_C_FAB_MS_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID),
		CONSTRAINT FK_C_FAB_MS_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_ROLE_ROLE (
		ROLE_ID VARCHAR2(20) NOT NULL,  -- 主鍵，不允許空值
		ROLE_TYPE VARCHAR2(40),         -- 允許空值
		ROLE_NAME VARCHAR2(40),         -- 允許空值
		ROLE_DESC VARCHAR2(240),           -- 允許空值，名稱保留為 "DESC"
		LM_USER VARCHAR2(40),           -- 允許空值
		LM_TIME TIMESTAMP(6),           -- 允許空值
		CONSTRAINT PK_C_ROLE_ROLE PRIMARY KEY (ROLE_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_ROLE_USR (
	  ROLE_ID VARCHAR2(20) NOT NULL,  -- 與 RoleEntity 中的 ROLE_ID 一致
	  USER_ID VARCHAR2(20) NOT NULL,
	  IS_UI_VISIBLE VARCHAR2(1),
	  LM_TIME TIMESTAMP(6) NULL,
	  LM_USER VARCHAR2(40),
	  CONSTRAINT PK_C_ROLE_USR PRIMARY KEY (ROLE_ID, USER_ID)
	);

	-- 添加外鍵約束
	alter table MSM.C_ROLE_USR
	add CONSTRAINT fk_role_id
	FOREIGN KEY (ROLE_ID) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_USR_USR (
		USER_ID VARCHAR2(20) NOT NULL,
		DEPT_CODE VARCHAR2(20),
		DEFAULT_ROLE_PLAY VARCHAR2(20),
		LAST_ROLE_PLAY VARCHAR2(20),
		USER_NAME VARCHAR2(40),
		USER_DESC VARCHAR2(240),
		IS_ACTIVE VARCHAR2(1),
		LM_TIME TIMESTAMP(6) DEFAULT SYSTIMESTAMP,
		CONSTRAINT PK_C_USR_USR PRIMARY KEY (USER_ID),
		CONSTRAINT FK_C_USR_USR_DEPT_CODE FOREIGN KEY (DEPT_CODE) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID),
		CONSTRAINT FK_C_USR_USR_DEFAULT_ROLE_PLAY FOREIGN KEY (DEFAULT_ROLE_PLAY) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID),
		CONSTRAINT FK_C_USR_USR_LAST_ROLE_PLAY FOREIGN KEY (LAST_ROLE_PLAY) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_ROLE_AUTHORITY (
	  ROLE_ID VARCHAR2(20) NOT NULL,
	  MS_ID VARCHAR2(20) NOT NULL,
	  FAB_ID VARCHAR2(20) NOT NULL,
	  APPLY_FORM_NUMBER VARCHAR2(20),
	  LM_USER VARCHAR2(40),
	  LM_TIME TIMESTAMP(6) NULL,
	  CONSTRAINT PK_C_ROLE_AUTHORITY PRIMARY KEY (ROLE_ID, MS_ID, FAB_ID),
	  CONSTRAINT FK_C_ROLE_AUTHORITY_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID),
	  CONSTRAINT FK_C_ROLE_AUTHORITY_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID),
	  CONSTRAINT FK_C_ROLE_AUTHORITY_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_ROLE_DEVICE (
		DEVICE_ID VARCHAR2(20) NOT NULL,
		ROLE_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(40) NOT NULL,
		DEVICE_NAME VARCHAR2(40),
		DEVICE_IP VARCHAR2(40) NOT NULL,
		DEVICE_DESC VARCHAR2(240),
		IS_ACTIVE VARCHAR2(1) NOT NULL,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_C_ROLE_DEVICE PRIMARY KEY (DEVICE_ID),
		CONSTRAINT FK_C_ROLE_DEVICE_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID),
		CONSTRAINT FK_C_ROLE_DEVICE_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_KEY_APIKEY (
		APIKEY_ID VARCHAR2(20) NOT NULL,
		ROLE_ID VARCHAR2(20) NOT NULL,
		KEY_NAME VARCHAR2(40),
		KEY_DESC VARCHAR2(240),
		IS_UI_VISIBLE VARCHAR2(1) DEFAULT 'N',
		IS_ACTIVE VARCHAR2(1) DEFAULT 'Y',
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CREATE_TIME TIMESTAMP(6),
		CONSTRAINT PK_C_KEY_APIKEY PRIMARY KEY (APIKEY_ID),
		CONSTRAINT FK_C_KEY_APIKEY_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_KEY_PERMISSION (
		APIKEY_ID VARCHAR2(20) NOT NULL,
		MS_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(20) NOT NULL,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CREATE_TIME TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
		CONSTRAINT PK_C_KEY_PERMISSION PRIMARY KEY (APIKEY_ID, MS_ID, FAB_ID),
		CONSTRAINT FK_C_KEY_PERMISSION_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID),
		CONSTRAINT FK_C_KEY_PERMISSION_APIKEY_ID FOREIGN KEY (APIKEY_ID) REFERENCES MSM.C_KEY_APIKEY(APIKEY_ID),
		CONSTRAINT FK_C_KEY_PERMISSION_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_GW_PLUGIN (
	  gw_plugin_id       VARCHAR2(20 CHAR) not null,
      gw_plugin_name     VARCHAR2(40 CHAR),
      gw_plugin_sample   VARCHAR2(2000 CHAR),
      gw_plugin_type     VARCHAR2(40 CHAR),
      lm_time            TIMESTAMP(6),
      lm_user            VARCHAR2(40 CHAR),
      gw_plugin_deploy   VARCHAR2(40 CHAR),
      gw_plugin_template VARCHAR2(100 CHAR)
	  CONSTRAINT PK_C_GW_PLUGIN PRIMARY KEY (GW_PLUGIN_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE msm.c_ms_plugin_dpy (
	  gw_plugin_id VARCHAR2(20) NOT NULL,
	  fab_id VARCHAR2(20) NOT NULL,
	  ms_id VARCHAR2(20) NOT NULL,
	  gw_plugin_parameter VARCHAR2(2000),
	  PRIMARY KEY (gw_plugin_id, ms_id,fab_id),
      CONSTRAINT FK_C_MS_PLUGIN_DPY_MS_ID FOREIGN KEY (ms_id) REFERENCES msm.c_ms_ms (ms_id)
      CONSTRAINT FK_C_MS_PLUGIN_DPY_FAB_ID FOREIGN KEY (fab_id) REFERENCES msm.c_fab_id (fab_id)
      CONSTRAINT FK_C_MS_PLUGIN_DPY_GWPLUGIN_ID FOREIGN KEY (gw_plugin_id ) REFERENCES msm.c_GW_PLUGIN (gw_plugin_id
    );

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.R_GW_UPSTREAM (
		GW_US_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(20) NOT NULL,
		SYSTEM_ID VARCHAR2(20) NOT NULL,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_R_GW_UPSTREAM PRIMARY KEY (GW_US_ID),
		CONSTRAINT FK_R_GW_UPSTREAM_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID),
		CONSTRAINT FK_R_GW_UPSTREAM_SYSTEM_ID FOREIGN KEY (SYSTEM_ID) REFERENCES MSM.C_SYS_SYSTEM(SYSTEM_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.R_GW_ROUTE (
		GW_ROUTE_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(20) NOT NULL,
		ENDPOINT_ID VARCHAR2(20) NOT NULL,
		LM_USER VARCHAR2(40),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_R_GW_ROUTE PRIMARY KEY (GW_ROUTE_ID),
		CONSTRAINT FK_R_GW_ROUTE_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID),
		CONSTRAINT FK_R_GW_ROUTE_ENDPOINT_ID FOREIGN KEY (ENDPOINT_ID) REFERENCES MSM.C_MS_ENDPOINT(ENDPOINT_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.R_APY_ROLE_AUTH (
		APPLY_FORM_ID VARCHAR2(20) NOT NULL,
		ROLE_ID VARCHAR2(20) NOT NULL,
		FORM_STATUS VARCHAR2(20),
		APPLICANT VARCHAR2(20),
		CREATE_TIME TIMESTAMP(6),
		LM_TIME TIMESTAMP(6),
		CONSTRAINT PK_R_APY_ROLE_AUTH PRIMARY KEY (APPLY_FORM_ID, ROLE_ID),
		CONSTRAINT FK_R_APY_ROLE_AUTH_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES MSM.C_ROLE_ROLE(ROLE_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.R_APY_ROLE_AUTH_DETAIL (
		APPLY_FORM_ID VARCHAR2(20) NOT NULL,
		MS_ID VARCHAR2(20) NOT NULL,
		FAB_ID VARCHAR2(20) NOT NULL,
		CONSTRAINT PK_R_APY_ROLE_AUTH_DETAIL PRIMARY KEY (APPLY_FORM_ID, MS_ID, FAB_ID),
		CONSTRAINT FK_R_APY_DETAIL_MS_ID FOREIGN KEY (MS_ID) REFERENCES MSM.C_MS_MS(MS_ID),
		CONSTRAINT FK_R_APY_DETAIL_FAB_ID FOREIGN KEY (FAB_ID) REFERENCES MSM.C_FAB_FAB(FAB_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_SYS_SIGNOFF_ADD (
		SYSTEM_ID VARCHAR2(20) NOT NULL,
		USER_ID VARCHAR2(20) NOT NULL,
		SIGNOFF_RANK NUMBER,
		CONSTRAINT PK_C_SYS_SIGNOFF_ADD PRIMARY KEY (SYSTEM_ID, USER_ID),
		CONSTRAINT FK_C_SYS_SIGNOFF_ADD_SYSTEM_ID FOREIGN KEY (SYSTEM_ID) REFERENCES MSM.C_SYS_SYSTEM(SYSTEM_ID)
	);

	-------------------------------------------------------------------------------------------------
	create TABLE msm.C_FAB_SIGNOFF_ADD(
	  fab_id VARCHAR2(20 CHAR) NOT NULL,
	  SITE_OWNER_ID VARCHAR2(20 CHAR) NOT NULL,
	  CONSTRAINT pk_C_FAB_SIGNOFF_ADD PRIMARY KEY (fab_id),
	  CONSTRAINT fk_C_FAB_SIGNOFF_ADD FOREIGN KEY (fab_id) REFERENCES msm.c_fab_fab (fab_id)
	);

    -- MSM 4.1.0
	-------------------------------------------------------------------------------------------------
	create TABLE MSM.C_EXT_CTL (
      EXT_ENTITY_ID VARCHAR2(40) PRIMARY KEY,
      EXT_ENTITY_KEY VARCHAR2(20),
      LM_TIME TIMESTAMP(6) DEFAULT SYSTIMESTAMP
    );
    -- MSM 4.1.0
	-------------------------------------------------------------------------------------------------
	create TABLE "MSM"."H_APY_ROLE_AUTH" (
        "APPLY_FORM_ID" VARCHAR2(20),
        "ROLE_ID" VARCHAR2(20),
        "FORM_STATUS" VARCHAR2(20),
        "APPLICANT" VARCHAR2(20),
        "CREATE_TIME" TIMESTAMP (6),
        "LM_TIME" TIMESTAMP (6),
        CONSTRAINT "SYS_C00428799" CHECK ("APPLY_FORM_ID" IS NOT NULL) ENABLE,
        CONSTRAINT "SYS_C00428800" CHECK ("ROLE_ID" IS NOT NULL) ENABLE,
        CONSTRAINT "PK_H_APY_ROLE_AUTH" PRIMARY KEY ("APPLY_FORM_ID", "ROLE_ID")
        USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING NOCOMPRESS
        CONSTRAINT "FK_H_APY_ROLE_AUTH_ROLE_ID" FOREIGN KEY ("ROLE_ID")
        REFERENCES "MSM"."C_ROLE_ROLE" ("ROLE_ID") ENABLE
    ) SEGMENT CREATION DEFERRED
    PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING

   -- MSM 4.1.0
	-------------------------------------------------------------------------------------------------
	CREATE TABLE "MSM"."H_APY_ROLE_AUTH_DETAIL" (
        "APPLY_FORM_ID" VARCHAR2(20),
        "MS_ID" VARCHAR2(20),
        "FAB_ID" VARCHAR2(20),
        CONSTRAINT "SYS_C00428807" CHECK ("APPLY_FORM_ID" IS NOT NULL) ENABLE,
        CONSTRAINT "SYS_C00428808" CHECK ("MS_ID" IS NOT NULL) ENABLE,
        CONSTRAINT "SYS_C00428809" CHECK ("FAB_ID" IS NOT NULL) ENABLE,
        CONSTRAINT "PK_H_APY_ROLE_AUTH_DETAIL" PRIMARY KEY ("APPLY_FORM_ID", "MS_ID", "FAB_ID")
        USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING NOCOMPRESS ,
        CONSTRAINT "FK_H_APY_DETAIL_FAB_ID" FOREIGN KEY ("FAB_ID")
        REFERENCES "MSM"."C_FAB_FAB" ("FAB_ID") ENABLE,
        CONSTRAINT "FK_H_APY_DETAIL_MS_ID" FOREIGN KEY ("MS_ID")
        REFERENCES "MSM"."C_MS_MS" ("MS_ID") ENABLE
    ) SEGMENT CREATION DEFERRED
    PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING

    -- MSM 4.1.2
	-------------------------------------------------------------------------------------------------
	CREATE TABLE "MSM"."C_USR_COMMITMENT" (
        "COMMITMENT_ID" VARCHAR2(20),
        "COMMITMENT_NAME" VARCHAR2(40),
        "COMMITMENT_CATEGORY" VARCHAR2(40),
        "COMMITMENT_DESC" VARCHAR2(240),
        "IS_ACTIVE" VARCHAR2(1),
        "CREATE_TIME" TIMESTAMP (6),
        CONSTRAINT "PK_C_USR_COMMITMENT" PRIMARY KEY ("COMMITMENT_ID")
    ) SEGMENT CREATION DEFERRED
    PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING

    -- MSM 4.1.2
	-------------------------------------------------------------------------------------------------
	CREATE TABLE "MSM"."C_USR_COMMITMENT_RECORD" (
        "COMMITMENT_ID" VARCHAR2(20),
        "USER_ID" VARCHAR2(20),
        "CREATE_TIME" TIMESTAMP (6),
        CONSTRAINT "PK_C_USR_COMMITMENT_RECORD" PRIMARY KEY ("COMMITMENT_ID", "USER_ID")
        USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 NOLOGGING NOCOMPRESS ,
        CONSTRAINT "FK_COMMITMENT_RECORD" FOREIGN KEY ("COMMITMENT_ID")
        REFERENCES "MSM"."C_USR_COMMITMENT" ("COMMITMENT_ID") ENABLE,
        CONSTRAINT "FK_COMMITMENT_RECORD_USER" FOREIGN KEY ("USER_ID")
        REFERENCES "MSM"."C_USR_USR" ("USER_ID") ENABLE
    ) SEGMENT CREATION DEFERRED
    PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS NOLOGGING


-- module Original Data
	insert all
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L3D', 'AUHY', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L5D', 'AUHY', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L6B', 'AULK', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L6B_SW', 'AULK', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('M02', 'AULK', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L5AB', 'AULT', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L4A', 'AULT', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L4A_LED', 'AULT', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('M01', 'AULT', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L3C', 'AUHC', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L6A', 'AUTCI', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('M11', 'AUTCI', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L5C', 'AUTCII', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L7A', 'AUTCII', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L7B', 'AUTCIII', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L8A', 'AUTCIII', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('P01', 'AUTCIII', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L8B', 'AUHL', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('C4A', 'AUTN', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('C5D', 'AUTN', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('C6C', 'AUTN', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('C5E', 'AUKH', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S01', 'AUSZ', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S02', 'AUSZ', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S06', 'AUSZ', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L6K', 'AUKS', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S11', 'AUXM', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S13', 'AUXM', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('S17', 'AUXM', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
	into msm.c_fab_fab (FAB_ID, SITE, LM_USER, LM_TIME) values ('L4B', 'AUST', 'Initial', to_timestamp('2024-03-22 06:56:08.842916', 'YYYY-MM-DD HH24:MI:SS.FF6'))
select * from dual;


--Grant CRUD Permission
	-- MSM 2.0.0
	grant select, insert, update, delete on msm.c_fab_fab to msm_ap;
	grant select, insert, update, delete on msm.c_role_role to msm_ap;
	grant select, insert, update, delete on msm.C_ROLE_USR to msm_ap;
	grant select, insert, update, delete on msm.c_sys_system to msm_ap;
	grant select, insert, update, delete on msm.c_MS_MS to msm_ap;
	grant select, insert, update, delete on msm.c_ms_endpoint to msm_ap;
	grant select, insert, update, delete on msm.c_role_authority to msm_ap;
	grant select, insert, update, delete on msm.c_sys_dpy to msm_ap;
	grant select, insert, update, delete on msm.c_ms_dpy to msm_ap;
	grant select, insert, update, delete on msm.c_key_apikey to msm_ap;
	grant select, insert, update, delete on msm.c_key_permission to msm_ap;
	grant select, insert, update, delete on msm.c_gw_plugin to msm_ap;
	grant select, insert, update, delete on msm.c_ms_plugin_dpy to msm_ap;
	grant select, insert, update, delete on msm.r_gw_route to msm_ap;
	grant select, insert, update, delete on msm.r_gw_upstream to msm_ap;
	grant select, insert, update, delete on msm.c_core_proxy to msm_ap;
	grant select, insert, update, delete on msm.c_fab_proxy to msm_ap;
	grant select, insert, update, delete on msm.c_net_ref to msm_ap;
	-- MSM 3.1.6
	grant select, insert, update, delete on msm.C_SYS_SIGNOFF_ADD to msm_ap;
	grant select, insert, update, delete on msm.C_MS_DOC to msm_ap;
	grant select, insert, update, delete on msm.C_USR_USR to msm_ap;
	grant select, insert, update, delete on msm.R_APY_ROLE_AUTH to msm_ap;
	grant select, insert, update, delete on msm.R_APY_ROLE_AUTH_DETAIL to msm_ap;
	grant select, insert, update, delete on msm.C_ROLE_DEVICE to msm_ap;
	grant select, insert, update, delete on msm.C_FAB_SIGNOFF_ADD to msm_ap;
	-- MSM 4.1.0
	grant select, insert, update, delete on msm.C_EXT_CTL to msm_ap;
	grant select, insert, update, delete on msm.H_APY_ROLE_AUTH to msm_ap;
	grant select, insert, update, delete on msm.H_APY_ROLE_AUTH_DETAIL to msm_ap;
	-- MSM 4.2.0
	grant select, insert, update, delete on msm.C_USR_COMMITMENT to msm_ap;
	grant select, insert, update, delete on msm.C_USR_COMMITMENT_RECORD to msm_ap;
