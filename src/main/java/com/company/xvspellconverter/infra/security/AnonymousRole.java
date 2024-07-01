package com.company.xvspellconverter.infra.security;

import com.company.xvspellconverter.infra.entity.Convert;
import com.company.xvspellconverter.infra.entity.Word;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Anonymous-role", code = AnonymousRole.CODE)
public interface AnonymousRole {
    String CODE = "anonymous-role";

    @MenuPolicy(menuIds = "AnonymousMainViewTopMenu")
    @ViewPolicy(viewIds = "AnonymousMainViewTopMenu")
    void screens();

    @EntityAttributePolicy(entityClass = Word.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Word.class, actions = EntityPolicyAction.READ)
    void wordEntity();

    @EntityAttributePolicy(entityClass = Convert.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Convert.class, actions = EntityPolicyAction.READ)
    void convert();

}