import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import com.checkpoint.rfid_raw_material.R
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogProviderInterface
import com.checkpoint.rfid_raw_material.utils.dialogs.interfaces.CustomDialogRemoveProviderInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomDialogRemoveProvider(context: Context): Dialog(context) {
    private lateinit var dialogRemoveProviderInterface: CustomDialogRemoveProviderInterface

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_remove_provider)
        val btnRemoveProvider = findViewById<Button>(R.id.btnAcceptRemoveProvider)
        val btnCloseDialogProvider = findViewById<FloatingActionButton>(R.id.btnCloseRemoveProvider)
        val btnCancelDialogProvider = findViewById<Button>(R.id.btnCancelRemoveProvider)

        dialogRemoveProviderInterface = context as CustomDialogRemoveProviderInterface

        btnCloseDialogProvider.setOnClickListener{
            dialogRemoveProviderInterface.closeDialogRemoveProvider()
        }

        btnCancelDialogProvider.setOnClickListener {
            dialogRemoveProviderInterface.closeDialogRemoveProvider()
        }

        btnRemoveProvider.setOnClickListener {
            dialogRemoveProviderInterface.removeProvider()
        }
    }
}