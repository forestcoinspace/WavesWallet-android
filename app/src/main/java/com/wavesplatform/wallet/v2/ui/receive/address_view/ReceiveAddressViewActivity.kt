package com.wavesplatform.wallet.v2.ui.receive.address_view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.wavesplatform.wallet.R
import com.wavesplatform.wallet.R.id.*
import com.wavesplatform.wallet.v2.data.model.remote.response.AssetBalance
import com.wavesplatform.wallet.v2.ui.address.MyAddressQrPresenter
import com.wavesplatform.wallet.v2.ui.base.view.BaseActivity
import com.wavesplatform.wallet.v2.ui.receive.invoice.InvoiceFragment
import com.wavesplatform.wallet.v2.ui.your_assets.YourAssetsActivity
import com.wavesplatform.wallet.v2.util.copyToClipboard
import kotlinx.android.synthetic.main.activity_receive_address_view.*
import pers.victor.ext.click
import pers.victor.ext.findDrawable
import pers.victor.ext.gone
import pers.victor.ext.visiable
import pyxis.uzuki.live.richutilskt.utils.runDelayed
import javax.inject.Inject

class ReceiveAddressViewActivity : BaseActivity(), ReceiveAddressView {

    @Inject
    @InjectPresenter
    lateinit var presenter: ReceiveAddressViewPresenter

    @ProvidePresenter
    fun providePresenter(): ReceiveAddressViewPresenter = presenter

    override fun configLayoutRes(): Int = R.layout.activity_receive_address_view

    override fun onViewReady(savedInstanceState: Bundle?) {
        val assetBalance = intent?.getParcelableExtra<AssetBalance>(YourAssetsActivity.BUNDLE_ASSET_ITEM)

        image_asset_icon.isOval = true
        image_asset_icon.setAsset(assetBalance)

        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        rotation.fillAfter = true
        image_loader.startAnimation(rotation)
        runDelayed(2000, {
            image_loader.clearAnimation()
            card_progress.gone()
            card_address_view.visiable()
            image_close.visiable()
        })

        image_close.click {
            finish()
        }
        button_close.click {
            finish()
        }
        frame_share.click {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text_address.text)
            startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.app_name)))
        }

        frame_copy.click {
            text_address.copyToClipboard()
        }
        image_copy.click {
            text_invoice_link.copyToClipboard()
        }
        image_share.click {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text_invoice_link.text)
            startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.app_name)))
        }
        if (intent.getBooleanExtra(InvoiceFragment.INVOICE_SCREEN, false)) {
            container_invoice_link.visiable()
            image_down_arrow.gone()

            image_asset_icon.setImageResource(R.drawable.logo_waves_48)
            toolbar_view.title = getString(R.string.receive_address_waves_address)
        }

        presenter.generateQRCode(text_address.text.toString(), resources.getDimension(R.dimen._200sdp).toInt())
    }

    override fun showQRCode(qrCode: Bitmap?) {
        image_view_recipient_action.setImageBitmap(qrCode)
    }
}