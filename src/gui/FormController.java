package gui;

/**
 * Created by ado on 15.12.15.
 */
public class FormController implements ChoosingController{
    private boolean choosing;
    public void enableChoosing(){
        choosing = true;
        FXMLWindowController.getInstance().onlyCivilianShipsEnabled();
        FXMLWindowController.getInstance().setChoosingState(true);
        FXMLWindowController.getInstance().setChoosingTarget(this);
    }

    @Override
    public void ChoiceHasBeenMade(String item) {

    }

}
