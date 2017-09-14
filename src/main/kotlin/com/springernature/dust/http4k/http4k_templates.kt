package com.springernature.dust.http4k

import com.springernature.dust.Dust
import com.springernature.dust.TemplateLoader
import com.springernature.dust.loadFromFilesIn
import com.springernature.dust.loadFromResourcesIn
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.Templates as Http4kTemplates


class DustTemplates : Http4kTemplates {
    override fun Caching(baseTemplateDir: String): TemplateRenderer {
        return dust4Http4K(true, loadFromFilesIn(baseTemplateDir))
    }
    
    override fun CachingClasspath(baseClasspathPackage: String): TemplateRenderer {
        return dust4Http4K(true, loadFromResourcesIn(baseClasspathPackage))
    }
    
    override fun HotReload(baseTemplateDir: String): TemplateRenderer {
        return dust4Http4K(false, loadFromFilesIn(baseTemplateDir))
    }
    
    private fun dust4Http4K(cacheTemplates: Boolean, loader: TemplateLoader): (ViewModel) -> String {
        val dust = Dust(cacheTemplates = cacheTemplates, loader = loader)
        
        return fun(viewModel: ViewModel) =
            dust.openTemplates().use { it[viewModel.template()](viewModel) }
    }
}
