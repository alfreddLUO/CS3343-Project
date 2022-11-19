package GoGoEat;

public class CommandMerchantModifyMenu implements Commands{
	
    private Merchants merchant;
    
    public CommandMerchantModifyMenu(Merchants merchant){
        this.merchant = merchant;
    }
    
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist {       
        
    	merchant.modifyMenu();
    	
    }
    
}
