package com.company.xvspellconverter.security;

import com.company.xvspellconverter.entity.Word;
import io.jmix.core.entity.KeyValueEntity;
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

    @MenuPolicy(menuIds = {"AnonymousMainViewTopMenu"})
    @ViewPolicy(viewIds = {"AnonymousMainViewTopMenu"})
    void screens();

    @EntityPolicy(entityClass = Word.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = Word.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void wordEntity();

}