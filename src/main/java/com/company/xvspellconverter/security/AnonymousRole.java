package com.company.xvspellconverter.security;

import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Anonymous-role", code = AnonymousRole.CODE)
public interface AnonymousRole {
    String CODE = "anonymous-role";

    @MenuPolicy(menuIds = {"AnonymousMainViewTopMenu"})
    @ViewPolicy(viewIds = {"AnonymousMainViewTopMenu"})
    void screens();

}