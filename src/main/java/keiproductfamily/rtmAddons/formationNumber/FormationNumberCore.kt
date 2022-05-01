package keiproductfamily.rtmAddons.formationNumber

import net.minecraftforge.common.DimensionManager
import org.apache.commons.lang3.StringUtils
import java.io.*
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

object FormationNumberCore {
    var isUpdate = false

    private val formationNumberMap = HashMap<Long, FormationNumberKeyPair>()
    fun getOrMake(formationID: Long): FormationNumberKeyPair {
        var ret = formationNumberMap[formationID]
        if (ret == null) {
            ret = FormationNumberKeyPair()
            formationNumberMap[formationID] = ret
        }
        return ret
    }

    fun set(formationID: Long, formationNumberKeyPair: FormationNumberKeyPair) {
        formationNumberMap[formationID] = formationNumberKeyPair
    }

    private const val FormationNumberMap = "FormationNumberMap.kei"
    private var rootDir: File by Delegates.notNull<File>()
    private fun setRootDir() {
        rootDir = File(DimensionManager.getCurrentSaveRootDirectory(), "KEI")
    }

    fun read(): Boolean {
        formationNumberMap.clear()

        setRootDir()
        rootDir.mkdirs()

        /////////////////
        // Compny List //
        /////////////////
        val saveDir = File(rootDir, FormationNumberMap)
        if (!saveDir.exists()) {
            try {
                saveDir.createNewFile()
                val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(saveDir), StandardCharsets.UTF_8))
                writer.write("")
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        try {
            val reader = BufferedReader(InputStreamReader(FileInputStream(saveDir), StandardCharsets.UTF_8))
            var str: String?
            while (reader.readLine().also { str = it } != null) {
                if (str!!.startsWith("#")) {
                    continue
                }
                str = str!!.trim { it <= ' ' }
                if (str!!.isNotEmpty()) {
                    val strArray = str!!.split(":")
                    val strFormationID = strArray[0].trim { it <= ' ' }
                    val strFormationNumber = strArray[1].trim { it <= ' ' }
                    if (StringUtils.isNumeric(strFormationID) && strFormationNumber.matches(Regex("[A-Z]-[0-9]{1,4}"))) {
                        val formationID = strFormationID.toLong()
                        val strFNs = strFormationNumber.split("-")
                        val formationNumberKeyPair = FormationNumberKeyPair(strFNs[0], strFNs[1])
                        formationNumberMap[formationID] = formationNumberKeyPair
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun save() {
        rootDir.mkdirs()
        val saveDir = File(rootDir, FormationNumberMap)
        if (!saveDir.exists()) {
            try {
                saveDir.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
        try {
            val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(saveDir), StandardCharsets.UTF_8))
            for ((formationID, formationNumber) in formationNumberMap) {
                val str = formationID.toString() + ":" + formationNumber.keyString + "\n"
                writer.write(str)
            }
            writer.flush()
            writer.close()
            //sender.addChatMessage(ChatComponentText("Company Data Save Completed!"))
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }
}