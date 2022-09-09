package com.example.services

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.services.databinding.FragmentDownloadBinding

class DownloadFragment : Fragment(R.layout.fragment_download) {

    private val vBinding: FragmentDownloadBinding by viewBinding()
    private val vModel: ViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateView()
        observeWorkInfo()

        vBinding.downloadButton.setOnClickListener {
            val url = vBinding.urlEditText.text.toString()
            vModel.startDownload(url)
        }
        vBinding.cancelButton.setOnClickListener {
            vModel.stopWork()
        }
        vBinding.retryDownloadBtn.setOnClickListener {
            val url = vBinding.urlEditText.text.toString()
            vModel.startDownload(url)
        }
    }

    private fun observeWorkInfo() {
        vModel.downloadLD.observe(viewLifecycleOwner) {
            WorkManager.getInstance(requireContext())
                .getWorkInfosForUniqueWorkLiveData(Worker.DOWNLOAD_WORK_ID)
                .observe(viewLifecycleOwner) { handleWorkInfo(it.first()) }
        }
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {
        val isFinished = workInfo.state.isFinished

        vBinding.urlEditText.isEnabled = isFinished
        vBinding.progressBarDownload.isVisible = !isFinished

        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> {
                vBinding.downloadButton.isVisible = false
                vBinding.progressBarDownload.isVisible = false
                vBinding.cancelButton.isVisible = true
                vBinding.retryDownloadBtn.isVisible = false
                vBinding.waitTextView.isVisible = true
                vBinding.waitTextView.text = getText(R.string.please_wait)
            }
            WorkInfo.State.RUNNING -> {
                vBinding.retryDownloadBtn.isVisible = false
                vBinding.downloadButton.isVisible = false
                vBinding.cancelButton.isVisible = true
                vBinding.waitTextView.isVisible = false
                vBinding.progressBarDownload.isVisible = true
            }
            WorkInfo.State.SUCCEEDED -> {
                vBinding.progressBarDownload.isVisible = false
                vBinding.cancelButton.isVisible = false
                vBinding.retryDownloadBtn.isVisible = false
                vBinding.waitTextView.isVisible = false
                vBinding.downloadButton.isVisible = true
                Toast.makeText(requireContext(), "Work finished success", Toast.LENGTH_SHORT).show()
            }
            WorkInfo.State.FAILED -> {
                vBinding.waitTextView.isVisible = true
                vBinding.waitTextView.text = workInfo.state.toString()
                vBinding.downloadButton.isVisible = false
                vBinding.cancelButton.isVisible = false
                vBinding.retryDownloadBtn.isVisible = true
                vBinding.progressBarDownload.isVisible = false
            }
            WorkInfo.State.CANCELLED -> {
                vBinding.progressBarDownload.isVisible = false
                vBinding.cancelButton.isVisible = false
                vBinding.retryDownloadBtn.isVisible = false
                vBinding.waitTextView.isVisible = false
                vBinding.downloadButton.isVisible = true
            }
            WorkInfo.State.BLOCKED -> {
                vBinding.waitTextView.isVisible = true
                vBinding.waitTextView.text = workInfo.state.toString()
                vBinding.downloadButton.isVisible = false
                vBinding.cancelButton.isVisible = false
                vBinding.retryDownloadBtn.isVisible = true
                vBinding.progressBarDownload.isVisible = false
            }
        }
    }

    private fun stateView() {
        vBinding.progressBarDownload.isVisible = false
        vBinding.cancelButton.isVisible = false
        vBinding.retryDownloadBtn.isVisible = false
        vBinding.waitTextView.isVisible = false
        vBinding.downloadButton.isVisible = true
    }
}