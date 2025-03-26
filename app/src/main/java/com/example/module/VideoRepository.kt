package com.example.module

object VideoRepository {
    private const val CLOUD_1 = "decewbra0" // Stores numbers & letters A-O
    private const val CLOUD_2 = "djnejwnqd" // Stores letters P-Z & words

    val videoMap: Map<String, Pair<String, String>> = mapOf(
        // Numbers (Stored in Cloud 1)
        "n0_front" to (CLOUD_1 to "n0_front_t8hyuj"),
        "n0_side" to (CLOUD_1 to "n0_side_qrkm8r"),
        "n1_front" to (CLOUD_1 to "n1_front_ugvwss"),
        "n1_side" to (CLOUD_1 to "n1_side_zay8ip"),
        "n2_front" to (CLOUD_1 to "n2_front_l5nezn"),
        "n2_side" to (CLOUD_1 to "n2_side_ce2t8x"),
        "n3_front" to (CLOUD_1 to "n3_front_dc6wuq"),
        "n3_side" to (CLOUD_1 to "n3_side_psknm1"),
        "n4_front" to (CLOUD_1 to "n4_front_hmypt4"),
        "n4_side" to (CLOUD_1 to "n4_side_zse7nv"),
        "n5_front" to (CLOUD_1 to "n5_front_xprh2d"),
        "n5_side" to (CLOUD_1 to "n5_side_kd32jo"),
        "n6_front" to (CLOUD_1 to "n6_front_s6lvbn"),
        "n6_side" to (CLOUD_1 to "n6_side_wiyn3t"),
        "n7_front" to (CLOUD_1 to "n7_front_tbs3z4"),
        "n7_side" to (CLOUD_1 to "n7_side_m8xneo"),
        "n8_front" to (CLOUD_1 to "n8_front_oitlxs"),
        "n8_side" to (CLOUD_1 to "n8_side_i7yjnd"),
        "n9_front" to (CLOUD_1 to "n9_front_bmvirh"),
        "n9_side" to (CLOUD_1 to "n9_side_edexse"),

        // Letters A-O (Stored in Cloud 1)
        "la_front" to (CLOUD_1 to "Front_view_of_A_m88oqu"),
        "la_side" to (CLOUD_1 to "Side_view_of_A_fg07sm"),
        "lb_front" to (CLOUD_1 to "Front_view_of_B_bsnqs6"),
        "lb_side" to (CLOUD_1 to "Side_view_of_B_psoyyi"),
        "lc_front" to (CLOUD_1 to "Front_view_of_C_ix3kew"),
        "lc_side" to (CLOUD_1 to "Side_view_of_C_ake9ol"),
        "ld_front" to (CLOUD_1 to "Front_view_of_D_mwouwf"),
        "ld_side" to (CLOUD_1 to "Side_view_of_D_biw6tq"),
        "le_front" to (CLOUD_1 to "Front_view_of_E_rayn4v"),
        "le_side" to (CLOUD_1 to "Side_view_of_E_oqyt9b"),
        "lf_front" to (CLOUD_1 to "Front_view_of_F_nwb11u"),
        "lf_side" to (CLOUD_1 to "Side_view_of_F_u9tay0"),
        "lg_front" to (CLOUD_1 to "Front_view_of_G_ymhosj"),
        "lg_side" to (CLOUD_1 to "Side_view_of_G_nflaor"),
        "lh_front" to (CLOUD_1 to "Front_view_of_H_yz5vlv"),
        "lh_side" to (CLOUD_1 to "Side_view_of_H_jtoges"),
        "li_front" to (CLOUD_1 to "Front_view_of_I_bdsqen"),
        "li_side" to (CLOUD_1 to "Side_view_of_I_xihpvb"),
        "lj_front" to (CLOUD_1 to "Front_view_of_J_dvutv8"),
        "lj_side" to (CLOUD_1 to "Side_view_of_J_fvhloh"),
        "lk_front" to (CLOUD_1 to "Front_view_of_K_ttjahc"),
        "lk_side" to (CLOUD_1 to "Side_view_of_K_r5zgqo"),
        "ll_front" to (CLOUD_1 to "Front_view_of_L_p557fy"),
        "ll_side" to (CLOUD_1 to "Side_view_of_L_fmqxtg"),
        "lm_front" to (CLOUD_1 to "Front_view_of_M_feospy"),
        "lm_side" to (CLOUD_1 to "Side_view_of_M_kgmaki"),
        "ln_front" to (CLOUD_1 to "Front_view_of_N_xp7fbw"),
        "ln_side" to (CLOUD_1 to "Side_view_of_N_umejav"),
        "lo_front" to (CLOUD_1 to "Front_view_of_O_xmjofs"),
        "lo_side" to (CLOUD_1 to "Side_view_of_O_pavts5"),

        // Letters P-Z (Stored in Cloud 2)
        "lp_front" to (CLOUD_2 to "Front_view_of_P_ydjq0w"),
        "lp_side" to (CLOUD_2 to ""),
        "lq_front" to (CLOUD_2 to "Front_view_of_Q_tozvif"),
        "lq_side" to (CLOUD_2 to ""),
        "lr_front" to (CLOUD_2 to "Front_view_of_R_s533vk"),
        "lr_side" to (CLOUD_2 to ""),
        "ls_front" to (CLOUD_2 to "Front_view_of_S_qgsw42"),
        "ls_side" to (CLOUD_2 to "Side_view_of_S_a7hxj9"),
        "lt_front" to (CLOUD_2 to "Front_view_of_T_yaeusx"),
        "lt_side" to (CLOUD_2 to "Side_view_of_T_jyir7f"),
        "lu_front" to (CLOUD_2 to "Front_view_of_U_u5ekpw"),
        "lu_side" to (CLOUD_2 to "Side_view_of_U_oqpz1k"),
        "lv_front" to (CLOUD_2 to "Front_view_of_V_rrxj1s"),
        "lv_side" to (CLOUD_2 to "Side_view_of_V_thjxus"),
        "lw_front" to (CLOUD_2 to "Front_view_of_W_anrqfw"),
        "lw_side" to (CLOUD_2 to "Side_view_of_W_cj2qrt"),
        "lx_front" to (CLOUD_2 to "Front_view_of_X_k5b6fs"),
        "lx_side" to (CLOUD_2 to "Side_view_of_X_zkiicu"),
        "ly_front" to (CLOUD_2 to "Front_view_of_Y_tdnds4"),
        "ly_side" to (CLOUD_2 to ""),
        "lz_front" to (CLOUD_2 to "Front_view_of_Z_a41qjc"),
        "lz_side" to (CLOUD_2 to "Side_view_of_Z_mrcs9n"),

        // Words (Stored in Cloud 2)
        "w_goodmorning_front" to (CLOUD_2 to "goodmorning_front_jc53wl"),
        "w_goodmorning_side" to (CLOUD_2 to "goodmorning_side_gsdumm"),
        "w_goodafternoon_front" to (CLOUD_2 to "goodafternoon_front_rkrymd"),
        "w_goodafternoon_side" to (CLOUD_2 to "goodafternoon_side_hjxtjy"),
        "w_goodevening_front" to (CLOUD_2 to "goodevening_front_ojicb4"),
        "w_goodevening_side" to (CLOUD_2 to "goodevening_side_exfh2a"),
        "w_takecare_front" to (CLOUD_2 to "takecare_front_hwkj2b"),
        "w_takecare_side" to (CLOUD_2 to "takecare_side_gzavo3"),
        "w_bye_front" to (CLOUD_2 to "bye_front_bwgbkj"),
        "w_bye_side" to (CLOUD_2 to "bye_side_t6xryk"),
        "w_help_front" to (CLOUD_2 to "help_front_asfqxy"),
        "w_help_side" to (CLOUD_2 to "help_side_p2ooq4"),
        "w_doctor_front" to (CLOUD_2 to "doctor_front_t2tfaj"),
        "w_doctor_side" to (CLOUD_2 to "doctor_side_nnnccb"),
        "w_hospital_front" to (CLOUD_2 to "hospital_front_lh1nxz"),
        "w_hospital_side" to (CLOUD_2 to "hospital_side_czrgz7"),
        "w_police_front" to (CLOUD_2 to "police_front_fl4npr"),
        "w_police_side" to (CLOUD_2 to "police_side_do9j9x"),
        "w_painful_front" to (CLOUD_2 to "painful_front_dosuvn"),
        "w_painful_side" to (CLOUD_2 to "painful_side_jzq6vj"),
        "w_emergency_front" to (CLOUD_2 to "emergency_front_kc3yfh"),
        "w_emergency_side" to (CLOUD_2 to "emergency_side_i6kgdg")
    )

    fun getCloudinaryUrl(videoCode: String, viewType: String): String {
        val key = "${videoCode}_$viewType"
        val (cloudName, publicId) = videoMap[key] ?: return ""
        return "https://res.cloudinary.com/$cloudName/video/upload/$publicId.mp4"
    }
}
