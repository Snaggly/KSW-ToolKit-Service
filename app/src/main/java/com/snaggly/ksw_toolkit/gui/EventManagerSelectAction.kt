package com.snaggly.ksw_toolkit.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.EventManagerSelectActionViewModel
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode

class EventManagerSelectAction(private val type: EventManagerTypes, private val actionEvent: EventManager.OnActionResult) : Fragment() {
    private lateinit var mViewModel: EventManagerSelectActionViewModel
    private lateinit var listKeyEvents: RecyclerView
    private lateinit var listApps: RecyclerView
    private lateinit var listMcuCommands: RecyclerView

    private lateinit var doNothingButton: RadioButton
    private lateinit var invokeKeyButton: RadioButton
    private lateinit var startAppButton: RadioButton
    private lateinit var mcuCommandsButton: RadioButton

    private lateinit var leaveToTopAnimation : Animation
    private lateinit var leaveToButtomAnimation : Animation
    private lateinit var enterFromTopAnimation : Animation
    private lateinit var enterFromButtomAnimation : Animation

    private var mode: ActionMode = ActionMode.DoNothing

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_manager_select_action_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EventManagerSelectActionViewModel::class.java)
        mViewModel.eventCurType = type
        mViewModel.actionEvent = actionEvent
        initRadioBtns()
        initLists()
        loadAnimations(300)
        doButtonEvents()
        preselectEvent()
        doNothingButton.requestFocus()
    }

    private fun initRadioBtns() {
        doNothingButton = requireView().findViewById(R.id.unnassignBtn)
        invokeKeyButton = requireView().findViewById(R.id.invokeKeyeventRadioButton)
        startAppButton = requireView().findViewById(R.id.startAppRadioButton)
        mcuCommandsButton = requireView().findViewById(R.id.sendMcuCommandBtn)
    }

    private fun initLists() {
        listKeyEvents = requireView().findViewById(R.id.availableKeyEventsListView)
        listApps = requireView().findViewById(R.id.availableAppsListView)
        listMcuCommands = requireView().findViewById(R.id.mcuCommandsListView)
        initRecyclerViews(listKeyEvents, context?.let { mViewModel.getListKeyEventsAdapter(it) }!!)
        initRecyclerViews(listApps, context?.let { mViewModel.getAvailableAppsAdapter(it) }!!)
        initRecyclerViews(listMcuCommands, context?.let { mViewModel.getMcuCommandsAdapter(it) }!!)
    }

    private fun loadAnimations(duration: Long) {
        leaveToTopAnimation = AnimationUtils.loadAnimation(context, R.anim.leave_to_top)
        leaveToTopAnimation.duration = duration
        leaveToButtomAnimation = AnimationUtils.loadAnimation(context, R.anim.leave_to_buttom)
        leaveToButtomAnimation.duration = duration
        enterFromTopAnimation = AnimationUtils.loadAnimation(context, R.anim.enter_from_top)
        enterFromTopAnimation.duration = duration
        enterFromButtomAnimation = AnimationUtils.loadAnimation(context, R.anim.enter_from_buttom)
        enterFromButtomAnimation.duration = duration
    }

    private fun initRecyclerViews(rv : RecyclerView, rvAdapterView: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = rvAdapterView
    }

    private fun doButtonEvents() {
        doNothingButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                when (mode) {
                    ActionMode.InvokeKeyEvent -> {
                        invokeKeyButton.isChecked = false
                        listKeyEvents.clearAnimation()
                        listKeyEvents.startAnimation(leaveToTopAnimation)
                        listKeyEvents.visibility = View.GONE
                    }
                    ActionMode.StartApp -> {
                        startAppButton.isChecked = false
                        listApps.clearAnimation()
                        listApps.startAnimation((leaveToTopAnimation))
                        listApps.visibility = View.GONE
                    }
                    ActionMode.SendMcuCommand -> {
                        mcuCommandsButton.isChecked = false
                        listMcuCommands.clearAnimation()
                        listMcuCommands.startAnimation((leaveToTopAnimation))
                        listMcuCommands.visibility = View.GONE
                    }
                    else -> {}
                }
                mode = ActionMode.DoNothing
                mViewModel.resetEvent()
                actionEvent.notifyView()
            }
        }
        invokeKeyButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                listKeyEvents.visibility = View.VISIBLE
                listKeyEvents.startAnimation(enterFromTopAnimation)

                when (mode) {
                    ActionMode.StartApp -> {
                        startAppButton.isChecked = false
                        listApps.clearAnimation()
                        listApps.visibility = View.GONE
                        listApps.startAnimation(leaveToButtomAnimation)
                    }
                    ActionMode.SendMcuCommand -> {
                        mcuCommandsButton.isChecked = false
                        listMcuCommands.clearAnimation()
                        listMcuCommands.visibility = View.GONE
                        listMcuCommands.startAnimation(leaveToButtomAnimation)
                    }
                    else -> doNothingButton.isChecked = false
                }

                listKeyEvents.requestFocus()
                mode = ActionMode.InvokeKeyEvent
            }
        }
        startAppButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                listApps.visibility = View.VISIBLE
                listApps.requestFocus()
                when (mode) {
                    ActionMode.InvokeKeyEvent -> {
                        invokeKeyButton.isChecked = false
                        listKeyEvents.clearAnimation()
                        listKeyEvents.startAnimation(leaveToTopAnimation)
                        listKeyEvents.visibility = View.GONE
                        listApps.startAnimation(enterFromButtomAnimation)
                    }
                    ActionMode.SendMcuCommand -> {
                        mcuCommandsButton.isChecked = false
                        listMcuCommands.clearAnimation()
                        listMcuCommands.startAnimation(leaveToButtomAnimation)
                        listMcuCommands.visibility = View.GONE
                        listApps.startAnimation(enterFromTopAnimation)
                    }
                    else -> {
                        doNothingButton.isChecked = false
                        listApps.startAnimation(enterFromTopAnimation)
                    }
                }

                listApps.requestFocus()
                mode = ActionMode.StartApp
            }
        }
        mcuCommandsButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                listMcuCommands.visibility = View.VISIBLE
                listMcuCommands.requestFocus()
                when (mode) {
                    ActionMode.InvokeKeyEvent -> {
                        invokeKeyButton.isChecked = false
                        listKeyEvents.clearAnimation()
                        listKeyEvents.startAnimation(leaveToTopAnimation)
                        listKeyEvents.visibility = View.GONE
                        listMcuCommands.startAnimation(enterFromButtomAnimation)
                    }
                    ActionMode.StartApp -> {
                        startAppButton.isChecked = false
                        listApps.clearAnimation()
                        listApps.startAnimation(leaveToTopAnimation)
                        listApps.visibility = View.GONE
                        listMcuCommands.startAnimation(enterFromButtomAnimation)
                    }
                    else -> {
                        doNothingButton.isChecked = false
                        listMcuCommands.startAnimation(enterFromTopAnimation)
                    }
                }

                listMcuCommands.requestFocus()
                mode = ActionMode.SendMcuCommand
            }
        }
    }

    private fun preselectEvent() {
        when(mViewModel.config?.eventMode){
            EventMode.KeyEvent -> invokeKeyButton.isChecked = true
            EventMode.StartApp -> startAppButton.isChecked = true
            EventMode.McuCommand -> mcuCommandsButton.isChecked = true
            else -> doNothingButton.isChecked = true
        }
    }

    companion object {
        fun newInstance(type: EventManagerTypes, actionEvent: EventManager.OnActionResult): EventManagerSelectAction? {
            return EventManagerSelectAction(type, actionEvent)
        }
    }

    enum class ActionMode {DoNothing, InvokeKeyEvent, StartApp, SendMcuCommand}
}