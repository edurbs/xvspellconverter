package com.company.xvspellconverter.infra.gateway;

import com.company.xvspellconverter.app.gateway.GetWordGateway;
import com.company.xvspellconverter.infra.entity.Word;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import io.jmix.core.DataManager;
import io.jmix.core.NoResultException;
import io.jmix.core.querycondition.PropertyCondition;
import org.springframework.stereotype.Component;

@Component
@AnonymousAllowed
public class GetWordRepositoryGateway implements GetWordGateway {

    private final DataManager dataManager;

    public GetWordRepositoryGateway(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public String getOneWordToByFrom(String word) {
        if(word==null){
            return "";
        }
        try{
            return dataManager.unconstrained().load(Word.class)
                    .condition(PropertyCondition.equal("from", word))
                    .one()
                    .getTo();
        }catch (NoResultException e){
            return "";
        }
    }

}
