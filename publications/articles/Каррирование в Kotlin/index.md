# Каррирование в Kotlin

Kotlin поддерживает парадигму функционального программирования (ФП).
Часто объектно ориентированный подход (ООП) ставят в противовес ФП, но они не соперники и могут дополнить друг друга.
Одно из понятий ФП это каррирование функций.
Каррирование может пригодиться при разработке на Kotlin и поможет упростить ваш код.
Приведу пример, который поможет понять возможное применение подобных функций.

### Аргументы

Для ООП мира привычно думать что функции могут иметь несколько аргументов.
На самом деле это не так.
Функция описывает связь между исходным (областью определения) и конечным множеством данных (областью значений).
Функция не связывает несколько исходных множеств с конечным множеством.
Таким образом, функция не может иметь несколько аргументов.
По сути, аргументы - это множество:

```kotlin
// Привычный вид функции
fun f(a: Int, b: Int) = a + b

// Скрытый вид функции
fun f(vararg a: Int) = a.first() + a.last()
```

Следуя соглашению, лишний код опущен. Но все же это функция одного аргумента, а не двух.

### Каррирование функций

Зная что аргумент один - массив данных (кортеж), функцию `f(a, b)` можно рассматривать как множество всех функций на N от множества всех функций на N.
Это выглядело бы так: `f(a)(b)`.
В таком случае можно записать так: `f(a) = f2; f2(b) = a + b`.
Когда применяется функция `f(a)` аргумент `a` перестает быть переменной и превращается
в константу для функции `f2(b)`.
Результатом выполнения `f(a)` является функция которую можно выполнить отложено.
Преобразование функции в вид `f(a)(b)` называется каррированием в честь математика Хаскелла Карри, хотя он это преобразование и не изобретал.

```kotlin
// Пример каррированной функции с применением fun
fun f(a: Int) = { b: Int -> a + b }

// Пример каррированной функции с применением переменной
val f2: (Int) -> (Int) -> Int = { b -> { a -> a + b } }
```

### Частичное применение

Частичное применение - это термин, который значит, что функция применяется частично в ожидании остальных аргументов.
Каррированые функции позволяют производить выполнение функции частями и передавать значения постепенно.

### Пример применения

Создам пустой проект на Jetpack Compose.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
```

Добавлю `BottomNavigation` для демонстрации.
Я добавляю пару табов, при клике по которым, будет выполняться какая-то работа.

```kotlin
MyApplicationTheme {

    var currentTab: Int by remember { mutableStateOf(0) }
    var currentText: String by remember { mutableStateOf("No click tab") }

    fun complexCalculation(tab: Int): String {
        return (tab + 1).toString()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentTab == 0,
                    icon = { Icon(Icons.Filled.Person, null) },
                    onClick = {
                        // set active tab
                        currentTab = 0
                        // complex calculation
                        currentText = complexCalculation(0)
                    }
                )
                BottomNavigationItem(
                    selected = currentTab == 1,
                    icon = { Icon(Icons.Filled.Phone, null) },
                    onClick = {
                        // set active tab
                        currentTab = 1
                        // complex calculation
                        currentText = complexCalculation(1)
                    }
                )
            }
        }
    ) { padding ->
        Text(
            modifier = Modifier.padding(padding),
            text = currentText
        )
    }
}
```

Сейчас мы имеем по 2 действия на каждый клик.
Не обращайте внимание на `currentTab: Int`, для подобных вещей лучше использовать `sealed class` но этот пример не про это.
Как мы можем упростить клик так что бы это занимало меньше кода? Добавить простую функцию и передать аргументы.

```kotlin
fun changeTab(tab: Int) {
    // set active tab
    currentTab = tab
    // complex calculation
    currentText = complexCalculation(tab)
}

Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
        BottomNavigation {
            BottomNavigationItem(
                selected = currentTab == 0,
                icon = { Icon(Icons.Filled.Person, null) },
                onClick = {
                    changeTab(0)
                }
            )
            BottomNavigationItem(
                selected = currentTab == 1,
                icon = { Icon(Icons.Filled.Phone, null) },
                onClick = {
                    changeTab(1)
                }
            )
        }
    }
) { padding ->
    Text(
        modifier = Modifier.padding(padding),
        text = currentText
    )
}
```

У нас сильно упрощенный вариант. Здесь я хочу показать лишь принцип работы.
Подобных complexCalculation может быть больше одного, и их можно выполнять в процессе, а не сразу всей кучей.
По мере поступления данных.
Но даже такой простой пример можно упростить.
В функции onClick нужно передать функцию без аргументов. Для этого нам нужна промежуточная функция и много скобок...
Давайте преобразуем `changeTab` в каррированную функцию которой уже не потребуются промежуточная функция.

```kotlin
fun changeTab(tab: Int): () -> Unit = {
    // set active tab
    currentTab = tab
    // complex calculation
    currentText = complexCalculation(tab)
}

Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
        BottomNavigation {
            BottomNavigationItem(
                selected = currentTab == 0,
                icon = { Icon(Icons.Filled.Person, null) },
                onClick = changeTab(0)
            )
            BottomNavigationItem(
                selected = currentTab == 1,
                icon = { Icon(Icons.Filled.Phone, null) },
                onClick = changeTab(1)
            )
        }
    }
) { padding ->
    Text(
        modifier = Modifier.padding(padding),
        text = currentText
    )
}
```

### Заключение

Возможности ФП в Kotlin дополняют ОПП подходы.
Функциональное программирование интересно, упрощает ваш ООП код.
Расширяет мировоззрение.
ФП это не что-то до нашей С++ эры, оно доступно вам сейчас и есть в ваших полюбившихся инструментах.
Используйте все доступные инструменты для лучшего кода.

Good luck!

Проект доступен на [GitHub](https://github.com/keygenqt/articles/tree/currying/project/)