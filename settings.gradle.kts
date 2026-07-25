pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Kakao 저장소는 맨 뒤에 배치하고 Kakao 전용 패키지만 조회하도록 설정
        maven { 
            url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/")
            content {
                includeGroup("com.kakao.maps.open")
                includeGroup("com.kakao.sdk")
            }
        }
    }
}

rootProject.name = "tesla-dash-K"
include(":app")
