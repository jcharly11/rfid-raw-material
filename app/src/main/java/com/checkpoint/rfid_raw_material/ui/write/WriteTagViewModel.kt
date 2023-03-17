package com.checkpoint.rfid_raw_material.ui.write

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.checkpoint.rfid_raw_material.handheld.BarcodeHandHeldInterface
import com.checkpoint.rfid_raw_material.handheld.kt.DeviceConfig
import com.checkpoint.rfid_raw_material.handheld.kt.HandHeldBarCodeReader
import com.checkpoint.rfid_raw_material.source.DataRepository
import com.checkpoint.rfid_raw_material.source.RawMaterialsDatabase
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.model.ProviderModel
import com.checkpoint.rfid_raw_material.zebra.BatteryHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ResponseHandlerInterface
import com.checkpoint.rfid_raw_material.zebra.ZebraRFIDHandlerImpl
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.SESSION
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class WriteTagViewModel (application: Application) : AndroidViewModel(application),BarcodeHandHeldInterface {
    private var repository: DataRepository
    private var handHeldBarCodeReader: HandHeldBarCodeReader? = null
    private val _liveCode: MutableLiveData<String> = MutableLiveData()
    var liveCode: LiveData<String> = _liveCode

    private val _deviceConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    var deviceConnected: LiveData<Boolean> = _deviceConnected

    @SuppressLint("StaticFieldLeak")
    private var context = application.applicationContext

    init {
        repository = DataRepository.getInstance(
            RawMaterialsDatabase.getDatabase(application.baseContext)
        )
        handHeldBarCodeReader = HandHeldBarCodeReader()
        handHeldBarCodeReader!!.setBarcodeResponseInterface(this)

    }

    suspend fun newProvider(id: Int,idAS:String,nameProvider:String): Provider = withContext(Dispatchers.IO) {
        repository.insertNewProvider(
            Provider(
                0,
                id,
                idAS,
                nameProvider
            )
        )
    }

    suspend fun getProviderList():MutableList<ProviderModel> = withContext(Dispatchers.IO){
        val list= repository.getProviders()
        var listProviders:MutableList<ProviderModel> = mutableListOf()

        list.iterator().forEachRemaining {
            var itemProvider= ProviderModel(id = it.id,it.name)
            listProviders!!.add(itemProvider)
        }
        listProviders= listProviders!!.toMutableList()
        listProviders
    }



    override fun setDataBarCode(code: String){

      _liveCode.value = code

    }

    override fun connected(status: Boolean) {
        _deviceConnected.value = status

    }

    suspend fun startHandHeldBarCode(){
        handHeldBarCodeReader!!.instance(context, DeviceConfig(
            0,
            SESSION.SESSION_S1,
            "RFD850019323520100189",
            ENUM_TRIGGER_MODE.BARCODE_MODE,
            ENUM_TRANSPORT.BLUETOOTH

        )
        )
    }

    suspend fun disconnectDevice(){
        viewModelScope.launch {
            handHeldBarCodeReader!!.disconnect()
        }
        }


    suspend fun newTag(version: String,subversion:String,type:String,piece:String,
    idProvider:Int,epc:String): Tags = withContext(Dispatchers.IO) {
        val nowDate: OffsetDateTime = OffsetDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        repository.insertNewTag(
            Tags(
                0,
                version,
                subversion,
                type,
                piece,
                idProvider,
                epc,
                formatter.format(nowDate)
            )
        )
    }

    suspend fun insertInitialProviders()= withContext(Dispatchers.IO){
        repository.deletePoviders()
        repository.insertNewProvider(Provider(0,869886,"1-10752","PALMIYE DOKUMA IPLIK TEKSTILS"))
        repository.insertNewProvider(Provider(0,864578,"1-10240","PROFITEX (SHENZHEN) INDUSTRIAL"))
        repository.insertNewProvider(Provider(0,817310,"1-04323","DONGKEUK TEXTILE CO"))
        repository.insertNewProvider(Provider(0,807317,"1-03132","CHANGZHOU DONGHENG PRINTING&DY"))
        repository.insertNewProvider(Provider(0,766346,"0-01625","ESTAMPACIÓN DIGITAL DEL MEDITERRANEO"))
        repository.insertNewProvider(Provider(0,736791,"1-08785","LISA S.P.A."))
        repository.insertNewProvider(Provider(0,958987,"1-13691","SHAOXING ZIYAN TEXTILE CO., LTD"))
        repository.insertNewProvider(Provider(0,742799,"1-00353","NORTHERN LINEN B.V."))
        repository.insertNewProvider(Provider(0,869674,"1-10519","SHENZHEN BOYANGTEX CO"))
        repository.insertNewProvider(Provider(0,730825,"0-00339","TEJIDOS REBES, S.L."))
        repository.insertNewProvider(Provider(0,784204,"1-05343","CHINA TEXTILES (SHEN ZHEN) CO."))
        repository.insertNewProvider(Provider(0,862293,"1-10133","NEON 1872 SRL"))
        repository.insertNewProvider(Provider(0,842887,"1-11015","BAKIRLAR TEKSTIL SANAYI VE TIC"))
        repository.insertNewProvider(Provider(0,746661,"1-03872","BAK-AY TEKSTIL SAN VE TIC AS"))
        repository.insertNewProvider(Provider(0,752147,"1-07318","HANGZHOU FUEN TEXTILE CO., LTD"))
        repository.insertNewProvider(Provider(0,864886,"1-10254","SHANGHAI FABRIC TECHNOLOGY CO."))
        repository.insertNewProvider(Provider(0,871119,"1-10598","RADIKA TEKSTIL PAZARLAMA VE TI"))
        repository.insertNewProvider(Provider(0,736592,"1-09702","LANIFICIO TESSILGODI, S.P.A."))
        repository.insertNewProvider(Provider(0,744781,"1-00618","CAN TEKSTIL ENTEGRE TESISLERI"))
        repository.insertNewProvider(Provider(0,738286,"1-09457","LANIFICIO COMATEX, S.P.A."))
        repository.insertNewProvider(Provider(0,739519,"1-00323","BTD TEKSTIL SAN. VE TIC. A.S"))
        repository.insertNewProvider(Provider(0,941487,"1-12550","HANGZHOU ZERO LANGUAGE TRADING"))
        repository.insertNewProvider(Provider(0,925551,"1-11636","SHAOXING JIANYOU TEXTILE CO"))
        repository.insertNewProvider(Provider(0,739387,"1-00629","BECAGLI"))
        repository.insertNewProvider(Provider(0,802772,"1-02334","UAB LININGAS"))
        repository.insertNewProvider(Provider(0,732301,"0-00584","CINCOTEX S.L."))
        repository.insertNewProvider(Provider(0,727520,"0-00760","CLOQUER S.A."))
        repository.insertNewProvider(Provider(0,731881,"0-01892","GARBANTEX, S.L."))
        repository.insertNewProvider(Provider(0,913687,"1-11158","KBC FASHION GMBH"))
        repository.insertNewProvider(Provider(0,732311,"0-01608","PERSENTILI INTERNACIONAL"))
        repository.insertNewProvider(Provider(0,728549,"0-02060","PREMIER TEX"))
        repository.insertNewProvider(Provider(0,736981,"1-09589","STUDIO ELLE S.N.C."))
        repository.insertNewProvider(Provider(0,938652,"0-02898","TAITEXTILS DESIGN"))
        repository.insertNewProvider(Provider(0,727896,"0-00381","TEXTIL SANTANDERINA"))
        repository.insertNewProvider(Provider(0,851021,"0-02688","RALIPTEX"))
        repository.insertNewProvider(Provider(0,734443,"0-00460","BOMBAY S.L"))
        repository.insertNewProvider(Provider(0,724660,"0-01919","CHEMITEX"))
        repository.insertNewProvider(Provider(0,778516,"1-11377","HANGZHOU XINSHENG PRINTING"))
        repository.insertNewProvider(Provider(0,736734,"1-09326","MANTECO S.P.A."))
        repository.insertNewProvider(Provider(0,845699,"1-07032","SHAOXING CITY EMB SENSE TEXTIL"))
        repository.insertNewProvider(Provider(0,797031,"1-08951","WUJIANG YUNZHU TEXTILE CO"))
        repository.insertNewProvider(Provider(0,876452,"1-10894","LE EUROPE SA"))
        repository.insertNewProvider(Provider(0,740695,"1-03584","QINGDAO MAX VOGUE IMP AND EXP"))
        repository.insertNewProvider(Provider(0,945102,"1-12765","MST LIMITED"))
        repository.insertNewProvider(Provider(0,0,"0-00990","GRAU"))
        repository.insertNewProvider(Provider(0,0,"1-11566","DFAL"))
        repository.insertNewProvider(Provider(0,0,"1-03626","OTOJAL"))
        repository.insertNewProvider(Provider(0,736617,"1-01735","NOVA FIDES"))
        repository.insertNewProvider(Provider(0,742019,"1-04043","TEXTIL LUSOIBERICA UNIPERSSOAL LDA"))
        repository.insertNewProvider(Provider(0,741311,"1-09190","PAULO DE OLIVEIRA, S.A."))
        repository.insertNewProvider(Provider(0,738606,"1-01821","SHAHI EXPORTS PVT .LTD."))
        repository.insertNewProvider(Provider(0,729410,"0-00796","FABRIL SEDERA, S.A"))
        repository.insertNewProvider(Provider(0,929330,"1-12739","WEIHAI MEIYUAN TEXTILE & GARME"))
        repository.insertNewProvider(Provider(0,764456,"1-02434","SMI TESSUTI S.P.A."))
        repository.insertNewProvider(Provider(0,931700,"1-12040","MANIFATTURA MAFFII DI MAFFII GABRIELE"))
        repository.insertNewProvider(Provider(0,729531,"0-00274","PREMAN S.L."))
        repository.insertNewProvider(Provider(0,737779,"1-02410","LANIFICIO BRUNETTO MORGANTI & C SPA"))
        repository.insertNewProvider(Provider(0,737653,"1-00808","LYRIA SPA"))
        repository.insertNewProvider(Provider(0,736511,"1-09340","MARZOTTO S.P.A."))
        repository.insertNewProvider(Provider(0,738246,"1-09634","MTT SPA-MANIF.TESSILE TOSCANA"))
        repository.insertNewProvider(Provider(0,736967,"1-09854","PONTOGLIO, S.P.A"))
        repository.insertNewProvider(Provider(0,845238,"1-03882","BIMS TEKSTIL SAN. VE TIC. LTD. STI."))
        repository.insertNewProvider(Provider(0,749287,"1-05985","GULIPEK DIS TICARET A.S"))
        repository.insertNewProvider(Provider(0,736757,"1-02435","MANIF.TESSILE PIEROZZI, S.R.L"))
        repository.insertNewProvider(Provider(0,830991,"1-07377","SINATEKS KUMAS DOKUMA VE ÖRME PAZ.SANAYITICARET"))
        repository.insertNewProvider(Provider(0,736673,"1-00617","A.G.M"))
        repository.insertNewProvider(Provider(0,926026,"1-11661","JRC IMPORTS LTD"))
        repository.insertNewProvider(Provider(0,737161,"1-00121","LANIFICIO PAULTEX, S.R.L."))
        repository.insertNewProvider(Provider(0,728541,"0-02027","NEW JUNIOR, S.A"))
        repository.insertNewProvider(Provider(0,736583,"1-09392","SAMPIETRO S.P.A."))
        repository.insertNewProvider(Provider(0,728858,"0-00509","FOLGAROLAS TEXTIL S.A."))
        repository.insertNewProvider(Provider(0,741133,"1-09045","DEVEAUX SAS"))
        repository.insertNewProvider(Provider(0,754589,"1-08341","ARSETEKS TEKSTIL TARIM KIMYA SAN VE PAZ LTD STI"))
        repository.insertNewProvider(Provider(0,829342,"1-12287","KOTON INSAAT TURIZM MENSUCAT SAN. VE TIC. LTD. STI."))
        repository.insertNewProvider(Provider(0,736724,"1-00270","LANIFICIO FRATELLI BALLI ,SPA"))
        repository.insertNewProvider(Provider(0,737786,"1-03541","MANIFATTURA DI CARMIGNANO SRL"))
        repository.insertNewProvider(Provider(0,934218,"1-12439","RICCIARINI TEKSTIL LIMITED SIRKETI"))
        repository.insertNewProvider(Provider(0,751678,"1-09022","Lanifico Nuovo Rivera"))
        repository.insertNewProvider(Provider(0,736658,"1-09069","TEXAPEL SPA"))
        repository.insertNewProvider(Provider(0,738010,"1-09642","TROUILLET CIE"))

    }
}