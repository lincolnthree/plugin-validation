/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.validation;

import java.io.File;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.validation.api.ValidationDescriptor;
import org.jboss.forge.validation.api.ValidationFacet;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

import static org.jboss.forge.project.dependencies.ScopeType.PROVIDED;

/**
 * @author Kevin Pollet
 */
@Alias("forge.spec.validation")
@RequiresFacet({ResourceFacet.class, DependencyFacet.class})
public class ValidationFacetImpl extends BaseFacet implements ValidationFacet
{
    private final Dependency beanValidationAPI;

    public ValidationFacetImpl()
    {
        this.beanValidationAPI = DependencyBuilder.create()
                .setGroupId("javax.validation")
                .setArtifactId("validation-api")
                .setVersion("1.0.0.GA")
                .setScopeType(PROVIDED);
    }

    @Override
    public ValidationDescriptor getConfig()
    {
        final FileResource<?> fileResource = getConfigFile();
        if (!fileResource.exists())
        {
            final DescriptorImporter<ValidationDescriptor> importer = Descriptors.importAs(ValidationDescriptor.class);
            return importer.from(getConfigFile().getResourceInputStream());
        }
        return null;
    }

    @Override
    public FileResource<?> getConfigFile()
    {
        final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        return resourceFacet.getResource("META-INF" + File.separator + "validation.xml");
    }

    @Override
    public void saveConfig(ValidationDescriptor descriptor)
    {
        final FileResource<?> fileResource = getConfigFile();
        if (!fileResource.exists())
        {
            fileResource.createNewFile();
        }
        fileResource.setContents(descriptor.exportAsString());
    }

    @Override
    public boolean install()
    {
        if (!isInstalled())
        {
            final DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
            dependencyFacet.addDependency(beanValidationAPI);
        }
        return true;
    }

    @Override
    public boolean isInstalled()
    {
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        return dependencyFacet.hasDependency(beanValidationAPI);
    }
}
