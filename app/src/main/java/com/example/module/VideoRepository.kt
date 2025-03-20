package com.example.module

object VideoRepository {
    private const val cloudName = "decewbra0"
    /*TODO
    private const val cloudName1 = ""
     */

    // Mapping for numbers, letters, and words
    val videoMap: Map<String, String> = mapOf(
        // Numbers mapping
        "n0_front" to "n0_front_t8hyuj",
        "n0_side" to "n0_side_qrkm8r",
        "n1_front" to "n1_front_ugvwss",
        "n1_side" to "n1_side_zay8ip",
        "n2_front" to "n2_front_l5nezn",
        "n2_side" to "n2_side_ce2t8x",
        "n3_front" to "n3_front_dc6wuq",
        "n3_side" to "n3_side_psknm1",
        "n4_front" to "n4_front_hmypt4",
        "n4_side" to "n4_side_zse7nv",
        "n5_front" to "n5_front_xprh2d",
        "n5_side" to "n5_side_kd32jo",
        "n6_front" to "n6_front_s6lvbn",
        "n6_side" to "n6_side_wiyn3t",
        "n7_front" to "n7_front_tbs3z4",
        "n7_side" to "n7_side_m8xneo",
        "n8_front" to "n8_front_oitlxs",
        "n8_side" to "n8_side_i7yjnd",
        "n9_front" to "n9_front_bmvirh",
        "n9_side" to "n9_side_edexse",
        // Letters mapping
        "la_front" to "tjac32fkpjvhis4lmjpd",
        "la_side" to "ivixpjsxtojwdp1np1v6"
    )

    fun getCloudinaryUrl(videoCode: String, viewType: String): String {
        val key = "${videoCode}_$viewType"
        val publicId = videoMap[key] ?: return ""
        return "https://res.cloudinary.com/$cloudName/video/upload/$publicId.mp4"
        //*TODO "https://res.cloudinary.com/$cloudName1/video/upload/$publicId.mp4"/
    }
}
