# 你好，HarmonyOS！

> 26-12-2025 <span class="github-link">[Материалы к статье :material-git:](https://gitcode.com/keygenqt_vz/ohos-gitcode)</span>

![image](../data/preview.png)

你好！ 我从事移动开发已经很久了。我为不同的平台使用过许多语言和框架进行开发，因此我想了解一下华为为开发者提供了什么。我编写了一个小巧但功能齐全的应用程序——GitCode Viewer，其中包含了实现一个典型应用程序所需的主要技术栈。在本文中，我将描述整个过程以及我对 ArkTS/ArkUI 的体验和印象。

### IDE

首先，我在[华为开发者联盟](https://developer.huawei.com/consumer/en)注册了账号，以便获取[DevEco Studio](https://developer.huawei.com/consumer/en/deveco-studio/)。你无法直接访问和下载——注册是强制性的。进入下载页面后，让我非常惊讶的是没有Linux版本，而Linux是我工作的主要操作系统。如果你没有其他操作系统，仍然可以在Linux上工作，只需安装[命令行工具](https://developer.huawei.com/consumer/en/doc/harmonyos-guides/ide-commandline-get)并在[Visual Studio Code](https://code.visualstudio.com/)中安装[ArkTS](https://github.com/ohosvscode/arkTS)插件：

![image](../data/ohos_linux.jpg)

但我决定不给自己凭空制造麻烦。操作系统对我来说不是问题。我有时会为Windows和macOS编写项目（只有在被迫的情况下才用Windows）。所以我直接安装了macOS（aarch64）版本，然后……再次感到惊讶。没有模拟器。看起来好像有，但我读到模拟器仅在亚太地区可用。没关系，我也有应对这种情况的解决方案——我提前在上海购买了一台搭载HarmonyOS NEXT的Mate 70 Pro。我就不详述如何安装IDE了，一切都很常规。

### Wish list

应该编写一个什么样的应用程序，既能尝试平台的大部分概念，又不必深入开发细节和基础设施搭建？我最近从GitHub迁移到了GitCode，并且想更深入地了解这项服务。GitCode有一个[REST API](https://docs.gitcode.com/v1-docs/en/docs/guide/)，可以在应用程序中使用——这就是HTTP请求。对于GitCode的OAuth，我们需要在应用程序中传递密钥。我喜欢通过浏览器进行OAuth——这样用户能获得最佳可用性，为此我们需要Deep Link。为了保存用户令牌，我们需要加密、数据存储、混淆。GitCode用户有代码仓库，这涉及列表、分页、刷新。为了学习数据状态管理，我们添加一个代码仓库页面——刷新页面时，列表中的数据会改变。如果有数据编辑功能就更好了，这样状态就需要沿着堆栈向下传递：`编辑 -> 查看 -> 列表`，嗯，这还是一个包含字段、验证和向服务器发送数据的表单。我还非常希望导航中有标签页，我是设计师，我觉得这样看才好。

愿望清单形成了。在我构思它的时候，我还不知道等待我的是什么，但希望是好的。提前剧透一下，我成功地完全实现了它，没有遇到太大问题。

### Create

打开DevEco Studio后，可以创建一个项目。我的理解是，DevEco Studio是JetBrains IDEA Community Edition的一个分支，为ArkTS/ArkUI工作进行了修改。总之，这是一个拥有经典界面的普通开发环境。我选择 `应用程序 -> 空能力` 并继续：

![image](../data/create.png)

填写项目配置后，我们得到了一个“Hello, world!”的应用程序。它可以被构建，但无法在设备上运行。需要在文件 `build-profile.json5` 中添加 `signingConfigs` —— 构建签名，以便可以将其安装到设备上。无论是调试版还是发布版，签名在任何情况下都是必需的。获取签名并不难——进入 `文件 -> 项目结构 -> 签名配置` 并再次在华为上进行身份验证：

![image](../data/create3.png)

授权后，我们只需点击“确定”，`signingConfigs` 字段就会自动填充。之后，你可以连接设备并点击熟悉的 ▶ 按钮，在设备上运行应用程序。如果你没有设备，有一个方便的预览模式：

![image](../data/create2.png)

> 我不认为你把调试密钥提交到git会有什么严重的后果，但最好不要这样做。文件 `build-profile.json5` 中有很多不同的设置，无法在 `.gitignore` 中排除它们。我没有对此做任何处理，但原则上可以通过[hvigor](https://developer.huawei.com/consumer/en/doc/harmonyos-guides/ide-hvigor)插件来解决。

### Structure

让我们看看应用程序模板是由什么组成的。通过 `空能力` 模板创建项目后，我得到了以下文件集：

```shell
.
├── AppScope
│   ├── app.json5
│   └── resources
│       └── base
│           ├── element
│           │   └── string.json
│           └── media
│               ├── background.png
│               ├── foreground.png
│               └── layered_image.json
├── build-profile.json5
├── code-linter.json5
├── entry
│   ├── build-profile.json5
│   ├── hvigorfile.ts
│   ├── obfuscation-rules.txt
│   ├── oh-package.json5
│   └── src
│       ├── main
│       │   ├── ets
│       │   │   ├── entryability
│       │   │   │   └── EntryAbility.ets
│       │   │   ├── entrybackupability
│       │   │   │   └── EntryBackupAbility.ets
│       │   │   └── pages
│       │   │       └── Index.ets
│       │   ├── module.json5
│       │   └── resources
│       │       ├── base
│       │       │   ├── element
│       │       │   │   ├── color.json
│       │       │   │   ├── float.json
│       │       │   │   └── string.json
│       │       │   ├── media
│       │       │   │   ├── background.png
│       │       │   │   ├── foreground.png
│       │       │   │   ├── layered_image.json
│       │       │   │   └── startIcon.png
│       │       │   └── profile
│       │       │       ├── backup_config.json
│       │       │       └── main_pages.json
│       │       ├── dark
│       │       │   └── element
│       │       │       └── color.json
│       │       └── rawfile
│       ├── mock
│       │   └── mock-config.json5
│       ├── ohosTest
│       │   ├── ets
│       │   │   └── test
│       │   │       ├── Ability.test.ets
│       │   │       └── List.test.ets
│       │   └── module.json5
│       └── test
│           ├── List.test.ets
│           └── LocalUnit.test.ets
├── hvigor
│   └── hvigor-config.json5
├── hvigorfile.ts
├── local.properties
├── oh-package-lock.json5
└── oh-package.json5
```

[JSON5](https://json5.org/) 是JSON文件格式的扩展。对于配置来说很方便：可以在其中添加注释，在数组末尾加逗号，以及其他便利之处。

应用程序被分为：“root”、“AppScope”、“entry”。项目根目录包含项目的全局配置。`AppScope` 包含应用程序图标、名称和主要信息：`bundleName`、`versionCode` 等。entry 是应用程序的主要模块，如果需要，可以有多个这样的模块。

包含 `hvigor-config.json5` 文件的 hvigor 目录负责构建配置。Hvigor 是 HarmonyOS 的构建系统，基于以 TypeScript 构建的增强型 Gradle 替代方案的概念。它不依赖于IDE，可通过命令行工具使用，因此组织CI/CD不会有问题。`hvigorfile.ts` 文件允许你通过插件自定义构建过程，并执行构建前后需要完成的任务。

构建配置文件在 `build-profile.json5` 文件中设置。它们允许灵活地为不同任务配置构建：测试、发布、调试等。

依赖项添加在 `oh-package.json5` 文件中，既可以在模块级别，也可以在项目级别。你可以在以下位置找到项目所需的包：[OpenHarmony 第三方库仓库](https://ohpm.openharmony.cn/#/en/home)。

其他重要的配置文件是：`code-linter.json5` 和 `obfuscation-rules.txt`。Linter 负责源代码的静态分析——自动检查错误、风格不一致和“不良”编程实践，充当自动代码审查者和编辑器，帮助编写更高质量、更简洁和更一致的代码。Obfuscation 将程序代码转换为难以阅读的混淆形式，保持程序的原始功能，但使其分析、理解和修改对人类和自动化工具来说极其困难。

应用程序代码及其资源位于 `entry->src->main`。`EntryAbility.ets` 是应用程序的入口点。可以在 `EntryBackupAbility.ets` 文件中配置应用程序的数据迁移。`Index.ets` 是应用程序页面，可以在 `EntryAbility.ets` 中重新分配。

应用程序资源：应用程序文本、其本地化、图像、浅色和深色主题的颜色以及其他数据位于 `resources` 目录中。每个模块可以包含自己的资源集，也可以将全局资源放在 `AppScope` 目录中，这些资源可以从所有模块访问。

在我看来，这一切都很简单、方便且经过深思熟虑。编写应用程序所需的一切都具备。让我们开始吧。

### Navigation

首先需要弄清楚页面导航。我希望主页面有标签页。[Tabs](https://developer.huawei.com/consumer/en/doc/harmonyos-references/ts-container-tabs) 组件可以实现它们：

```ts
Tabs({ barPosition: BarPosition.End }) {
  TabContent() {
	// Page 1
  }.tabBar(customTabBar({ icon: $r('app.media.icon_home'), active: this.tab === 0 }))

  TabContent() {
    // Page 2
  }.tabBar(customTabBar({ icon: $r('app.media.icon_page_info'), active: this.tab === 1 }))
}
.onSelected((index: number) => {
  this.tab = index;
})
.height('100%')
.width('100%')
.backgroundColor($r('sys.color.background_primary'))
.barBackgroundColor(Color.Transparent)
.barOverlap(true)
```

可以通过 `@State` 控制页面的状态。变量 `this.tab` 是这样添加的：

```ts
@State page: number = 1;
```

`customTabBar` 函数修改标签页，让设计师满意。你不能在UI堆栈中随意调用函数。只有那些标有 `@Builder` 注解的函数才可以：

```ts
@Builder
export function customTabBar(params: CustomTabBarParams) {
  Button({ type: ButtonType.Circle }) {
    Image(params.icon)
      .fillColor(params.active ? $r('app.color.primary') : $r('app.color.btn_round_icon_fill'))
      .width(24)
      .height(24)
  }.width(40).height(40).backgroundColor($r('app.color.btn_round_background'))
}
```

`CustomTabBarParams` 是参数类。需要注意的是，当使用自己的函数时，如果没有使用类传递参数，那么在父组件状态改变后参数不会更新；而使用类时，更新会发生。它看起来像这样：

```ts
class CustomTabBarParams {
  icon: ResourceStr | null = null;
  active: boolean = false;
}
```

我们可以在 `TabContent` 中放入我们的页面。我的标签页中有两个页面——带有导航的主页和“设置/关于”页面。第二个页面很简单，但主页将承担所有导航的负载。[Navigation](https://developer.huawei.com/consumer/en/doc/harmonyos-references/ts-basic-components-navigation) 组件可以实现页面间的导航：

```ts
@Provide('navStack') navStack: NavPathStack = new NavPathStack()

build() {
	Tabs({ barPosition: BarPosition.End }) {
	  TabContent() {
	    Navigation(this.navStack) {
	      // tabRepos
	    }
	    .title($r('app.string.app_name'))
	    .mode(NavigationMode.Stack)
	    .navDestination(navDestination)
	  }.tabBar(customTabBar({ icon: $r('app.media.icon_home'), active: this.tab === 0 }))

	  TabContent() {
	    // tabAbout
	  }.tabBar(customTabBar({ icon: $r('app.media.icon_page_info'), active: this.tab === 1 }))
	}
	.onSelected((index: number) => {
	  this.tab = index;
	})
	.height('100%')
	.width('100%')
	.backgroundColor($r('sys.color.background_primary'))
	.barBackgroundColor(Color.Transparent)
	.barOverlap(true)
}
```

但 Navigation 并不知道标签页的存在。也就是说，我们的主页在标签页中，当沿着堆栈导航到另一个页面时，标签页会保持在原地，这不是我希望的。但可以修复：

```ts
build() {
	Tabs({ barPosition: BarPosition.End }) {
	  TabContent() {
	    Navigation(this.navStack) {
	      // tabRepos
	    }
	    .title($r('app.string.app_name'))
	    .mode(NavigationMode.Stack)
	    .navDestination(navDestination)
	  }.tabBar(customTabBar({ icon: $r('app.media.icon_home'), active: this.tab === 0 }))

	  TabContent() {
	    // tabAbout
	  }.tabBar(customTabBar({ icon: $r('app.media.icon_page_info'), active: this.tab === 1 }))
	}
	.onSelected((index: number) => {
	  this.tab = index;
	})
	.height('100%')
	.width('100%')
	.backgroundColor($r('sys.color.background_primary'))
	.barBackgroundColor(Color.Transparent)
	.barOverlap(true)
	// Change the height when the stack size changes
	.barHeight(this.navStack.size() === 0 ? null : 0.01)
	// Let's disable swipe
	.scrollable(false)
	// Let's add a height change animation
	.animation({
	  duration: 200,
	  iterations: 1,
	  curve: Curve.Linear,
	  playMode: PlayMode.Normal
	})
}
```

现在，当我们从主页沿着页面堆栈导航时，标签页会优雅地“折叠”起来，并且只会在我的应用程序中包含 GitCode 代码仓库列表的主页上显示。可以从页面沿着堆栈导航，如下所示：

```ts
this.navStack?.pushPath({ name: Pages.repo });
```

`Pages` 是页面名称的常量。[NavPathStack](https://developer.huawei.com/consumer/en/doc/harmonyos-references/ts-basic-components-navigation#navpathstack10) 允许进行导航、传递数据，以及导航所需的一切。需要为 Navigation 组件在 `navDestination` 函数中指定导航逻辑。我的这个函数如下所示：

```ts
@Builder
export function navDestination(name: string, param: string) {
  if (name === Pages.repo) {
    PageRepo();
  } else if (name === Pages.repo_update) {
    PageRepoUpdate();
  } else if (name === Pages.user) {
    PageUser();
  } else {
    PageNotFound();
  }
}
```

当我们调用导航中的导航方法时，我们会进入此函数。处理传入参数（如果需要），然后导航到我们需要的页面。导航中的嵌套页面可以通过以下方式访问导航：

```ts
@Consume('navStack') navStack: NavPathStack;

build() {
  NavDestination() {
    // Body page
  }.title($r('app.string.PageRepoUpdate_title')).onBackPressed(() => {
    this.navStack.pop();
    return true;
  })
}
```

通过扩展这一点，你可以获得你所需的导航。通过导航传递参数会锁定值，在我的应用程序中，每个页面都会更新数据，为了在堆栈中向上更新数据，我使用了全局UI状态存储。

### AppStorage & PersistentStorage

[AppStorage](https://developer.huawei.com/consumer/en/doc/harmonyos-guides/arkts-appstorage#storagelink) —— 允许使用数据，并全局改变其状态。例如，代码仓库列表位于 AppStorage 中，我们导航到编辑某个代码仓库，更新它，然后可以访问该列表并更改其中的数据。当导航回堆栈时，列表将被更新。

[PersistentStorage](https://developer.huawei.com/consumer/en/doc/harmonyos-guides/arkts-persiststorage) —— 这是 AppStorage 的补充，允许在本地保存数据。这不是数据库，其使用不意味着高负载，但对于保存令牌或其他小数据量数据来说，它完全够用。

为了将 AppStorage 添加到组件中，使用 `@StorageLink` 代替 `@State`：

```ts
@StorageLink(Storage.repoData) repoData: RepoModel | null = null;
@StorageLink(Storage.reposData) reposData: [RepoModel] | null = null;
```

这里 `Storage` 是包含名称的常量，`RepoModel` 是稍后我们通过 GitCode API 请求获取的数据模型。你可以将 AppStorage 添加到任何需要的页面，并在所有页面上同时更改其状态。

为了保存数据，我们只需初始化 PersistentStorage。在应用程序中，我保存用户数据和授权数据：

```ts
PersistentStorage.persistProp(Storage.authData, null);
PersistentStorage.persistProp(Storage.userData, null);
```

重要的是要注意，PersistentStorage 的初始化不应该在任何地方进行，而应该在主页进行。但不能以未加密的形式存储授权数据。让我们对其进行加密。

### Obfuscation & Crypto

默认情况下，混淆是禁用的，可以在文件 `build-profile.json5` 中激活它：

```json5
"buildOptionSet": [
  {
    "name": "release",
    "arkOptions": {
      "obfuscation": {
        "ruleOptions": {
          "enable": true,
          "files": [
            "./obfuscation-rules.txt"
          ]
        }
      }
    }
  },
],
```

文件 `obfuscation-rules.txt` 允许你配置[混淆](https://developer.huawei.com/consumer/en/doc/harmonyos-guides-V5/source-obfuscation-V5)。这降低了应用程序被破解以及获取应用程序中使用的密钥的风险。我们需要这样一个密钥来加密将存储在 PersistentStorage 中的数据。

为了加密数据，我们可以使用 `@ohos/crypto-js` 包。为此，请更新文件 `oh-package.json5` 中的依赖项：

```json5
{
  "name": "entry",
  "version": "0.0.1",
  "description": "Application for HarmonyOS on ArkTS and ArkUI working with the GitCode REST API.",
  "main": "",
  "author": "Vitaliy Zarubin <keygenqt@yandex.ru",
  "license": "Apache-2.0",
  "dependencies": {
    "@ohos/crypto-js": "2.0.5"
  }
}
```

我们添加几个函数，用于加密数据：

```ts
import { CryptoJS } from '@ohos/crypto-js'
import BuildProfile from 'BuildProfile';

export function encryptData(data: string) {
  return CryptoJS.AES.encrypt(data, BuildProfile.cryptoSecret).toString();
}

export function decryptData(encryptedData: string) {
  return CryptoJS.AES.decrypt(encryptedData, BuildProfile.cryptoSecret).toString(CryptoJS.enc.Utf8);
}
```

为了方便，为 `AuthModel` 添加保存和获取数据并包含加密的功能：

```ts
import { Storage } from '../constants';
import { decryptData, encryptData } from '../utils/crypto';
import { AuthModel } from './AuthModel';

export function saveAuthModel(model: AuthModel) {
  AppStorage.setOrCreate(Storage.authData, encryptData(JSON.stringify(model)));
}

export function getAuthModel(): AuthModel | undefined {
  const data = AppStorage.get<string>(Storage.authData);
  if (!data) return undefined;
  return JSON.parse(decryptData(data));
}
```

现在我们可以确信数据已加密，并且没有人可以获取它们。但是 `BuildProfile.cryptoSecret` 从哪里来呢？

### BuildProfile & Plugin Hvigor

[BuildProfile](https://developer.huawei.com/consumer/en/doc/harmonyos-guides/ide-hvigor-get-build-profile-para-guide) 允许将构建参数中的变量传递到应用程序中：

```ts
/**
 * Use these variables when you tailor your ArkTS code. They must be of the const type.
 */
export const BUNDLE_NAME = 'com.keygenqt.gitcode';
export const BUNDLE_TYPE = 'app';
export const VERSION_CODE = 1000000;
export const VERSION_NAME = '0.0.1';
export const TARGET_NAME = 'default';
export const PRODUCT_NAME = 'default';
export const BUILD_MODE_NAME = 'debug';
export const DEBUG = true;

/**
 * BuildProfile Class is used only for compatibility purposes.
 */
export default class BuildProfile {
	static readonly BUNDLE_NAME = BUNDLE_NAME;
  static readonly BUNDLE_TYPE = BUNDLE_TYPE;
  static readonly VERSION_CODE = VERSION_CODE;
  static readonly VERSION_NAME = VERSION_NAME;
  static readonly TARGET_NAME = TARGET_NAME;
  static readonly PRODUCT_NAME = PRODUCT_NAME;
  static readonly BUILD_MODE_NAME = BUILD_MODE_NAME;
  static readonly DEBUG = DEBUG;
}
```

我们可以通过 `buildProfileFields` 在 `build-profile.json5` 文件中添加自定义字段，以便在运行时访问它们。它们会被混淆，我认为这足够安全，可以保存我们的秘密密钥（如果你知道更好的方法，请在评论中告诉我）。

我添加了 3 个字段：`clientId`、`clientSecret`、`cryptoSecret`。前两个是在 GitCode 上注册应用程序时获得的，`cryptoSecret` 是用于数据加密的密钥：

```json5
"products": [
  {
    "name": "default",
    "signingConfig": "default",
    "targetSdkVersion": "5.1.1(19)",
    "compatibleSdkVersion": "5.1.1(19)",
    "runtimeOS": "HarmonyOS",
    "buildOption": {
      "strictMode": {
        "caseSensitiveCheck": true,
        "useNormalizedOHMUrl": true
      },
      "arkOptions": {
        "buildProfileFields": {
          "clientId": "***",
          "clientSecret": "***",
          "cryptoSecret": "***",
        }
      }
    },
  }
]
```

将秘密密钥直接放在 `build-profile.json5` 文件中不是一个好主意：它们很快会进入 git 历史记录。为了防止这种情况发生，我们可以编写一个 Hvigor 插件，它将读取位于 `.gitignore` 中的文件，并在构建时设置这些值。在文件 `hvigorfile.ts` 中添加插件：

```ts
import { appTasks, OhosPluginId } from '@ohos/hvigor-ohos-plugin';
import { hvigor, HvigorNode, HvigorPlugin } from '@ohos/hvigor';

function localSecretPlugin(): HvigorPlugin {
  return {
    pluginId: 'localSecretPlugin',
    apply(node: HvigorNode) {
      hvigor.getRootNode().afterNodeEvaluate(root => {
        const fs = require('fs');
        const config = './local.secret.json5';
        if (fs.existsSync(config)) {
          const data = fs.readFileSync(config, 'utf-8');
          const secret = JSON.parse(data);
          const appCtx = root.getContext(OhosPluginId.OHOS_APP_PLUGIN) as OhosAppContext;
          const buildProfile = appCtx.getBuildProfileOpt();
          (buildProfile.app.products || []).forEach((prd: any) => {
            prd.buildOption.arkOptions.buildProfileFields.clientId = secret.clientId;
            prd.buildOption.arkOptions.buildProfileFields.clientSecret = secret.clientSecret;
            prd.buildOption.arkOptions.buildProfileFields.cryptoSecret = secret.cryptoSecret;
          });
          appCtx.setBuildProfileOpt(buildProfile);
        } else {
          console.warn(`> Not found file '${config}'`)
        }
      });
    }
  }}

export default {
  system: appTasks, plugins: [localSecretPlugin()]
}
```

它将读取我们添加到 `.gitignore` 中的 `local.secret.json5` 文件，并设置所需的值。

```json5
{
  "clientId": "vyxtzcgg4i4aivmiua9eom71qkao1jp4t6e2",
  "clientSecret": "eivbesrcri8w71zm0tmtskdck0u0czj83dfo",
  "cryptoSecret": "vtl3q9dkl0zgxerp9obol1gym9clitbo5s0n"
}
```

构建后，BuildProfile 将变为：

```ts
/**
 * Use these variables when you tailor your ArkTS code. They must be of the const type.
 */
export const BUNDLE_NAME = 'com.keygenqt.gitcode';
export const BUNDLE_TYPE = 'app';
export const VERSION_CODE = 1000000;
export const VERSION_NAME = '0.0.1';
export const TARGET_NAME = 'default';
export const PRODUCT_NAME = 'default';
export const BUILD_MODE_NAME = 'debug';
export const DEBUG = true;
export const clientId = 'vyxtzcgg4i4aivmiua9eom71qkao1jp4t6e2';
export const clientSecret = 'eivbesrcri8w71zm0tmtskdck0u0czj83dfo';
export const cryptoSecret = 'vtl3q9dkl0zgxerp9obol1gym9clitbo5s0n';

/**
 * BuildProfile Class is used only for compatibility purposes.
 */
export default class BuildProfile {
	static readonly BUNDLE_NAME = BUNDLE_NAME;
  static readonly BUNDLE_TYPE = BUNDLE_TYPE;
  static readonly VERSION_CODE = VERSION_CODE;
  static readonly VERSION_NAME = VERSION_NAME;
  static readonly TARGET_NAME = TARGET_NAME;
  static readonly PRODUCT_NAME = PRODUCT_NAME;
  static readonly BUILD_MODE_NAME = BUILD_MODE_NAME;
  static readonly DEBUG = DEBUG;
  static readonly clientId = clientId;
	static readonly clientSecret = clientSecret;
	static readonly cryptoSecret = cryptoSecret;
}
```

字段 `clientId` 和 `clientSecret` 是执行带 GitCode 授权的请求所必需的，用于 [REST API](https://docs.gitcode.com/v1-docs/en/docs/guide/)。你可以通过创建 GitCode OAuth 应用程序来获取它们。

### OAuth GitCode

为了授权 GitCode API 的请求，需要在 GitCode 个人资料设置中创建一个应用程序：

![image](../data/auth_app.png)

在相邻的选项卡中，你可以找到你订阅的应用程序——如果需要，可以在那里取消订阅。设置很简单，但有一个重要的字段——重定向链接（应用回调地址）。为了在应用程序中注册，我们将用户重定向到授权页面，成功授权后，GitCode 会将用户重定向到带有数据的重定向页面，应用程序应处理此页面。

应用程序可以通过两种方式处理此类重定向：WebView 或 Deep Link。WebView 是为穷人准备的方式。它很简单，但主要缺点是用户总是需要在资源上进行授权。外部浏览器有保存功能，用户可能已经登录了资源，他们不必每次都输入登录名和密码。我的选择是 Deep Link 和外部浏览器。

### Deeplink

为了添加 Deep Link，我们需要一个网站，将用户从浏览器重定向到应用程序。我不会教你写网站，一个简单的 HTML 页面就足够了，它会重定向到 Deep Link。GitCode 本身不允许你在重定向字段中指定 Deep Link：

```html
<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>Welcome!</title>
	<style>
		/* ... */
	</style>
</head>
<body onload="doOpenLink()">
	<table class="Root-Table">
		<tr class="Root-Tr">
			<td class="Root-Td">
				<div class="Box">
					<h1> Welcome! </h1>
					<div class="Icon">
						<img src="https://raw.gitcode.com/keygenqt_vz/foreground.png"/>
					</div>
					<div style="margin-bottom: 30px;">
						Authorization was successful, to complete it, go to the application.
					</div>
					<div onclick="doOpenLink()" class="Link">
						Go to the application
					</div>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>

<script>
	function doOpenLink() {
		window.location.href = "link://ohos-gitcode.keygenqt.com" + window.location.search
	}
</script>
```

页面上的 Deep Link 是 `link://ohos-gitcode.keygenqt.com`，重定向会自动发生，并且如果用户忽略了自动重定向，他们也可以通过点击按钮进行跳转。为了在应用程序中添加 Deep Link，我们在文件 `module.json5` 中指定我们希望打开主机 `ohos-gitcode.keygenqt.com`：

```json5
{
//...
"abilities": [
  {
    "name": "EntryAbility",
    "srcEntry": "./ets/entryability/EntryAbility.ets",
    "description": "$string:EntryAbility_desc",
    "icon": "$media:layered_image",
    "label": "$string:EntryAbility_label",
    "startWindowIcon": "$media:layered_start_icon",
    "startWindowBackground": "$color:start_window_background",
    "exported": true,
    "skills": [
      {
        "entities": [
          "entity.system.home"
        ],
        "actions": [
          "ohos.want.action.home",
          "ohos.want.action.viewData",
          "ohos.want.action.sendToData"
        ],
        "uris": [
          {
            "scheme": "link",
            "host": "ohos-gitcode.keygenqt.com"
          }
        ]
      }
    ]
  }
]
//...
}
```

之后，当尝试打开 Deep Link `link://ohos-gitcode.keygenqt.com` 时，会提示我们的应用程序。可以在 `EntryAbility` 类的 `onNewWant` 方法中接收重定向：

```ts
export default class EntryAbility extends UIAbility {
	// ...
  onNewWant(want: Want, launchParam: AbilityConstant.LaunchParam) {
    let uri = want?.uri;
    if (uri && uri.includes("ohos-gitcode.keygenqt.com")) {
      let urlObject = url.URL.parseURL(want?.uri);
      let code = urlObject.params.get('code');
      // Auth user by code...
    }
  }
  // ...
}
```

GitCode 重定向到为跳转指定的网站。该网站将打开带有来自重定向的授权代码的 Deep Link。获得它后，可以执行请求并获取令牌，然后可以使用这些令牌访问 API。让我们发出请求并获取授权数据。

### HTTP request

对于请求，我使用了 `@ohos/axios` 包——一个用于处理网络请求的库。为了执行请求，需要在 `module.json5` 中添加权限：

```json5
{
  "module": {
	  // ...
    "requestPermissions": [
      {
        "name": "ohos.permission.INTERNET",
        "reason": "$string:reason_permission_internet",
        "usedScene": {
          "abilities": ["EntryAbility"],
          "when": "always"
        }
      }
    ]
    // ...
  }
}
```

现在可以在 `onNewWant` 方法中完成授权：

```ts
export default class EntryAbility extends UIAbility {
	// ...
  onNewWant(want: Want, launchParam: AbilityConstant.LaunchParam) {
    let uri = want?.uri;
    if (uri && uri.includes("ohos-gitcode.keygenqt.com")) {
      let urlObject = url.URL.parseURL(want?.uri);
      let code = urlObject.params.get('code');
      // Auth by code
      axios.post<AuthModel, AxiosResponse<AuthModel>, null>(Links.auth_token(code))
        .then((response: AxiosResponse<AuthModel>) => {
          // Save auth data
          saveAuthModel(response.data);
          // Get user data
          axios.get<UserModel, AxiosResponse<UserModel>, null>(Links.user())
            .then((response: AxiosResponse<UserModel>) => {
              // Save user data
              saveUserModel(response.data);
            })
            .catch((_: AxiosError) => AppStorage.delete(Storage.userData))
        })
        .catch((_: AxiosError) => AppStorage.delete(Storage.authCode))
    }
  }
  // ...
}
```

在这里，我们执行 POST 请求以获取令牌。我在 `Links` 类的方法中生成链接：

```ts
export class Links {
  static auth_token(code: string | null) {
    return `https://gitcode.com/oauth/token?grant_type=authorization_code&code=${code}&client_id=${BuildProfile.clientId}&client_secret=${BuildProfile.clientSecret}`;
  }

  static user(): string {
    const link = `https://api.gitcode.com/api/v5/user?access_token=${getAuthModel()?.access_token}`;
    console.debug(link)
    return link
  }
}
```

`axios` 接受模型接口并在成功执行时返回模型：

```ts
export interface AuthModel {
  access_token: string,
  expires_in: number,
  refresh_token: string,
  scope: string,
  created_at: string,
}
```

我们保存带有加密的授权模型，并执行获取用户数据的请求。在获取所有必要数据并将其保存到 AppStorage 后，具有 `@StorageLink` 的页面会更改数据状态，我们可以通过更改页面状态来响应。

> `axios` 包运行良好，它很方便，但我发现了一个不便之处……它不支持 `PATCH` 方法（[issues/361](https://gitcode.com/openharmony-sig/ohos_axios/issues/361)）。我在 REST API 中很少看到 `PATCH` 方法，但 GitCode 中有它。为了一个请求——保存设置，我使用了 [rcp](https://developer.huawei.com/consumer/cn/doc/harmonyos-references/remote-communication-rcp#section20787153164810) 来执行它。希望这个问题能在 `axios` 中得到解决。

为了控制页面状态，我编写了一个组件，解决了每个页面上代码重复的问题——`PageBodyState`。

### Page State

每个页面都有网络请求以获取最新数据。请求可能成功或失败，令牌可能过期，页面需要等待响应。对于所有这些任务，我创建了一个所有页面通用的组件，用于显示所有必要的状态：

```ts
import lottie from '@ohos/lottie';
import { PageState } from "../enums"
import { common } from '@kit.AbilityKit';
import { Links, Storage } from '../constants';
import { UserModel } from '../models';

@Component
export struct PageBodyState {
  @Require
  @Prop  state: PageState | object | null;

  @Require
  @BuilderParam content: () => void

  @StorageLink(Storage.authCode) authCode: boolean = false;
  @StorageLink(Storage.userData) userData: UserModel | null = null;

  stateAuth: () => void = () => {}
  stateAuthLoading: () => void = () => {}
  stateLoading: () => void = () => {}
  stateError: () => void = () => {}

  build() {
    if (!this.authCode && this.userData === null) {
      Column() {
        this.bodyAuth()
      }
      .width('100%')
      .height('100%')
      .onAttach(() => {
        this.state = PageState.Loading
        this.stateAuth()
      })
    }
    else if (this.authCode && this.userData === null) {
      Column() {
        this.bodyLoading()
      }
      .width('100%')
      .height('100%')
      .onAttach(this.stateAuthLoading)
    }
    else if (this.state === PageState.Loading) {
      Column() {
        this.bodyLoading()
      }
      .width('100%')
      .height('100%')
      .onAttach(this.stateLoading)
    }
    else if (this.state === PageState.Error) {
      Column() {
        this.bodyError()
      }
      .width('100%')
      .height('100%')
      .onAttach(this.stateError)
    } else {
      Column() {
        this.content()
      }
      .width('100%')
      .height('100%')
    }
  }

  @Builder
  bodyAuth() {
    // Body auth
  }

  @Builder
  bodyLoading() {
    // Body loading
  }

  @Builder
  bodyError() {
     // Body error
  }
}
```

该组件也可以监听 AppStorage 中的数据状态并显示相应的内容。我们还可以通过页面应用 `PageBodyState` 来管理状态：

```ts
import axios, { AxiosError, AxiosResponse } from "@ohos/axios";
import { common } from '@kit.AbilityKit';
import { PageBodyState } from "../../base/components";
import { Links, Storage } from "../../base/constants";
import { PageState } from "../../base/enums";
import { UserModel } from "../../base/models";

@Component
export struct PageUser {
  @Consume('navStack') navStack: NavPathStack;

  @StorageLink(Storage.userData) userData: UserModel | null = null;

  @State pageState: PageState = PageState.Loading;

  build() {
    NavDestination() {
      PageBodyState({state: this.pageState, stateLoading: () => {
        this.queryModel()
      }}) {
        this.bodyModel()
      }
    }
    .title($r('app.string.PageUser_title'))
    .onBackPressed(() => this.navStack.pop() !== undefined)
  }

  queryModel() {
    axios.get<UserModel, AxiosResponse<UserModel>, null>(Links.user())
      .then((response: AxiosResponse<UserModel>) => {
        this.userData = response.data
        this.pageState = PageState.Ready
      })
      .catch((_: AxiosError) => this.pageState = PageState.Error);
  }

  @Builder
  bodyModel() {
    Refresh({ refreshing: this.pageState == PageState.Refresh }) {
      Scroll() {
        // Body content
      }
    }
    .height('100%')
    .onStateChange((refreshStatus: RefreshStatus) => {
      if (refreshStatus == RefreshStatus.Refresh) {
        this.pageState = PageState.Refresh
        this.queryModel()
      }
    })
    .refreshOffset(64)
    .pullToRefresh(true)
  }
}
```

打开 `PageUser` 页面时，我们从 `PageState.Loading` 状态开始，`PageBodyState` 调用 `stateLoading` 方法并执行获取用户数据的请求。根据结果，我们将状态设置为 `PageState.Ready` 或 `PageState.Error`。ArkUI 组件 [Refresh](https://developer.huawei.com/consumer/en/doc/harmonyos-references/ts-container-refresh) 允许在不更改全局状态的情况下刷新页面，为此有 `PageState.Refresh`。这样，我们消除了代码重复，并通过一个组件标准化了页面状态的处理方法。

> 请注意成功请求后更新数据 `this.userData = response.data`——它们将在整个应用程序中更新。

现在，我们拥有了发展应用程序所需的所有基本构建块。所有的 ArkUI 组件都可以在[文档](https://developer.huawei.com/consumer/en/doc/harmonyos-references/arkui-declarative-comp)中找到——文档相当详细并附有示例。

### Done

我没有描述整个应用程序，它[已在 GitCode 上提供](https://gitcode.com/keygenqt_vz/ohos-gitcode)供自行研究。我指出了基本的细微差别。是时候分享总体印象了——这很酷。不是没有问题，有些概念很奇怪，但我曾经为类似的演示应用程序在 iOS 和 Android 上所做的一切，都在 ArkTS/ArkUI 上毫不费力地实现了。要编写应用程序，你需要一部手机和 macOS（Windows 不算，他们自己想办法吧）——缺少它们可能会成为问题。文档有三种语言，这很棒，不是很多公司能做到这一点。尽管文档中有示例，但在互联网上找到一些内容很困难，文章很少，并且在国外资源上语言被严格标记为 TypeScript——显然，他们不知道还有 ArkTS。我只是浅浅地接触了这个平台，但总体感觉很好，同志们，你们走在了正确的道路上。
