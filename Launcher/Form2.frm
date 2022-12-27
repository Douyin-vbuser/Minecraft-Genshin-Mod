VERSION 5.00
Begin VB.Form Form2 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Setting"
   ClientHeight    =   5130
   ClientLeft      =   45
   ClientTop       =   390
   ClientWidth     =   5895
   Icon            =   "Form2.frx":0000
   LinkTopic       =   "Form2"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   5130
   ScaleWidth      =   5895
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.TextBox Text6 
      Height          =   975
      Left            =   6240
      MultiLine       =   -1  'True
      TabIndex        =   12
      Top             =   1680
      Width           =   3255
   End
   Begin VB.CommandButton Command2 
      Cancel          =   -1  'True
      Caption         =   "取消"
      BeginProperty Font 
         Name            =   "宋体"
         Size            =   12
         Charset         =   134
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   495
      Left            =   3600
      TabIndex        =   11
      Top             =   4560
      Width           =   975
   End
   Begin VB.CommandButton Command1 
      Caption         =   "保存"
      BeginProperty Font 
         Name            =   "宋体"
         Size            =   12
         Charset         =   134
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   495
      Left            =   4680
      TabIndex        =   10
      Top             =   4560
      Width           =   1095
   End
   Begin VB.TextBox Text5 
      Height          =   975
      Left            =   6240
      MultiLine       =   -1  'True
      TabIndex        =   9
      Top             =   480
      Width           =   3255
   End
   Begin VB.Frame Frame3 
      Caption         =   "Minecraft设置"
      BeginProperty Font 
         Name            =   "宋体"
         Size            =   10.5
         Charset         =   134
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   2295
      Left            =   120
      TabIndex        =   6
      Top             =   2040
      Width           =   5655
      Begin VB.TextBox Text8 
         Height          =   270
         Left            =   4080
         TabIndex        =   18
         Top             =   1200
         Width           =   1335
      End
      Begin VB.TextBox Text7 
         Height          =   270
         Left            =   1320
         TabIndex        =   16
         Top             =   1200
         Width           =   1335
      End
      Begin VB.TextBox Text3 
         Height          =   270
         Left            =   1920
         TabIndex        =   14
         Top             =   840
         Width           =   3495
      End
      Begin VB.TextBox Text4 
         Height          =   270
         Left            =   1920
         TabIndex        =   8
         Top             =   360
         Width           =   3495
      End
      Begin VB.Label Label6 
         Caption         =   "窗口高："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   375
         Left            =   2880
         TabIndex        =   17
         Top             =   1200
         Width           =   1935
      End
      Begin VB.Label Label5 
         Caption         =   "窗口宽："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   375
         Left            =   120
         TabIndex        =   15
         Top             =   1200
         Width           =   1935
      End
      Begin VB.Label Label3 
         Caption         =   "Java地址："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   375
         Left            =   120
         TabIndex        =   13
         Top             =   840
         Width           =   1935
      End
      Begin VB.Label Label4 
         Caption         =   "Minecraft地址："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   375
         Left            =   120
         TabIndex        =   7
         Top             =   360
         Width           =   1935
      End
   End
   Begin VB.Frame Frame2 
      Caption         =   "启动器地址"
      BeginProperty Font 
         Name            =   "宋体"
         Size            =   10.5
         Charset         =   134
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   855
      Left            =   120
      TabIndex        =   3
      Top             =   1080
      Width           =   5655
      Begin VB.TextBox Text2 
         Height          =   270
         Left            =   1920
         Locked          =   -1  'True
         TabIndex        =   5
         Top             =   360
         Width           =   3495
      End
      Begin VB.Label Label2 
         Caption         =   "启动器地址："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   375
         Left            =   120
         TabIndex        =   4
         Top             =   360
         Width           =   1575
      End
   End
   Begin VB.Frame Frame1 
      Caption         =   "用户"
      BeginProperty Font 
         Name            =   "宋体"
         Size            =   10.5
         Charset         =   134
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   855
      Left            =   120
      TabIndex        =   0
      Top             =   120
      Width           =   5655
      Begin VB.TextBox Text1 
         Height          =   270
         Left            =   1920
         TabIndex        =   2
         Top             =   360
         Width           =   3495
      End
      Begin VB.Label Label1 
         Caption         =   "用户名："
         BeginProperty Font 
            Name            =   "宋体"
            Size            =   12
            Charset         =   134
            Weight          =   400
            Underline       =   0   'False
            Italic          =   0   'False
            Strikethrough   =   0   'False
         EndProperty
         Height          =   255
         Left            =   120
         TabIndex        =   1
         Top             =   360
         Width           =   1335
      End
   End
End
Attribute VB_Name = "Form2"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Command1_Click()
Text6.Text = "" & Text1.Text & "" & Chr(13) + Chr(10) & "" & Text4.Text & "" & Chr(13) + Chr(10) & "" & Text3.Text & "" & Chr(13) + Chr(10) & "" & Text7.Text & "" & Chr(13) + Chr(10) & "" & Text8.Text & ""
Open "" & App.Path & "\property.txt" For Output As #1
Print #1, Text6.Text
Close #1
Open "" & App.Path & "\start.bat" For Output As #1
temp1 = "@echo off" & Chr(10) + Chr(13) & "title 启动 - Minecraft Impact" & Chr(10) + Chr(13) & "echo Start" & Chr(10) + Chr(13) & "Set APPDATA = " & Text4.Text & "\" & Chr(10) + Chr(13) & "cd /D " & Text4.Text & "\" & Chr(10) + Chr(13) & "" & Text3.Text & " -XX:+UseG1GC -XX:-UseAdaptiveSizePolicy "
temp2 = "-XX:-OmitStackTraceInFastThrow -Dfml.ignoreInvalidMinecraftCertificates=True -Dfml.ignorePatchDiscrepancies=True -Dlog4j2.formatMsgNoLookups=true -Djava.net.preferIPv4Stack=true -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Xmn256m -Xmx512m -Djava.library.path=" & Text4.Text & "\versions\Modtester\Modtester-natives -cp " & Text4.Text & "\libraries\com\mojang\patchy\1.2.3\patchy-1.2.3.jar;" & Text4.Text & "\libraries\oshi-project\oshi-core\1.1\oshi-core-1.1.jar;" & Text4.Text & "\libraries\net\java\dev\jna\jna\4.4.0\jna-4.4.0.jar;" & Text4.Text & "\libraries\net\java\dev\jna\platform\3.4.0\platform-3.4.0.jar;" & Text4.Text & "\libraries\com\ibm\icu\icu4j-core-mojang\51.2\icu4j-core-mojang-51.2.jar;" & Text4.Text & "\libraries\net\sf\jopt-simple\jopt-simple\5.0.3\jopt-simple-5.0.3.jar;" & Text4.Text & "\libraries\com\paulscode\codecjorbis\20101023\codecjorbis-20101023.jar;" & Text4.Text & "\libraries\com\paulscode\codecwav\20101023\codecwav-20101023.jar;"
temp3 = "" & Text4.Text & "\libraries\com\paulscode\libraryjavasound\20101123\libraryjavasound-20101123.jar;" & Text4.Text & "\libraries\com\paulscode\librarylwjglopenal\20100824\librarylwjglopenal-20100824.jar;" & Text4.Text & "\libraries\com\paulscode\soundsystem\20120107\soundsystem-20120107.jar;" & Text4.Text & "\libraries\io\netty\netty-all\4.1.9.Final\netty-all-4.1.9.Final.jar;" & Text4.Text & "\libraries\com\google\guava\guava\21.0\guava-21.0.jar;" & Text4.Text & "\libraries\org\apache\commons\commons-lang3\3.5\commons-lang3-3.5.jar;" & Text4.Text & "\libraries\commons-io\commons-io\2.5\commons-io-2.5.jar;" & Text4.Text & "\libraries\commons-codec\commons-codec\1.10\commons-codec-1.10.jar;" & Text4.Text & "\libraries\net\java\jinput\jinput\2.0.5\jinput-2.0.5.jar;" & Text4.Text & "\libraries\net\java\jutils\jutils\1.0.0\jutils-1.0.0.jar;" & Text4.Text & "\libraries\com\google\code\gson\gson\2.8.0\gson-2.8.0.jar;" & Text4.Text & "\libraries\com\mojang\authlib\1.5.25\authlib-1.5.25.jar;"
temp4 = "" & Text4.Text & "\libraries\com\mojang\realms\1.10.22\realms-1.10.22.jar;" & Text4.Text & "\libraries\org\apache\commons\commons-compress\1.8.1\commons-compress-1.8.1.jar;" & Text4.Text & "\libraries\org\apache\httpcomponents\httpclient\4.3.3\httpclient-4.3.3.jar;" & Text4.Text & "\libraries\commons-logging\commons-logging\1.1.3\commons-logging-1.1.3.jar;" & Text4.Text & "\libraries\org\apache\httpcomponents\httpcore\4.3.2\httpcore-4.3.2.jar;" & Text4.Text & "\libraries\it\unimi\dsi\fastutil\7.1.0\fastutil-7.1.0.jar;" & Text4.Text & "\libraries\org\apache\logging\log4j\log4j-api\2.8.1\log4j-api-2.8.1.jar;" & Text4.Text & "\libraries\org\apache\logging\log4j\log4j-core\2.8.1\log4j-core-2.8.1.jar;" & Text4.Text & "\libraries\org\lwjgl\lwjgl\lwjgl\2.9.4-nightly-20150209\lwjgl-2.9.4-nightly-20150209.jar;" & Text4.Text & "\libraries\org\lwjgl\lwjgl\lwjgl_util\2.9.4-nightly-20150209\lwjgl_util-2.9.4-nightly-20150209.jar;"
temp5 = "" & Text4.Text & "\libraries\com\mojang\text2speech\1.10.3\text2speech-1.10.3.jar;" & Text4.Text & "\libraries\net\minecraftforge\forge\1.12.2-14.23.5.2847\forge-1.12.2-14.23.5.2847.jar;" & Text4.Text & "\libraries\net\minecraft\launchwrapper\1.12\launchwrapper-1.12.jar;" & Text4.Text & "\libraries\org\ow2\asm\asm-all\5.2\asm-all-5.2.jar;" & Text4.Text & "\libraries\org\jline\jline\3.5.1\jline-3.5.1.jar;" & Text4.Text & "\libraries\com\typesafe\akka\akka-actor_2.11\2.3.3\akka-actor_2.11-2.3.3.jar;" & Text4.Text & "\libraries\com\typesafe\config\1.2.1\config-1.2.1.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-actors-migration_2.11\1.1.0\scala-actors-migration_2.11-1.1.0.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-compiler\2.11.1\scala-compiler-2.11.1.jar;" & Text4.Text & "\libraries\org\scala-lang\plugins\scala-continuations-library_2.11\1.0.2\scala-continuations-library_2.11-1.0.2.jar;"
temp6 = "" & Text4.Text & "\libraries\org\scala-lang\plugins\scala-continuations-plugin_2.11.1\1.0.2\scala-continuations-plugin_2.11.1-1.0.2.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-library\2.11.1\scala-library-2.11.1.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-parser-combinators_2.11\1.0.1\scala-parser-combinators_2.11-1.0.1.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-reflect\2.11.1\scala-reflect-2.11.1.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-swing_2.11\1.0.1\scala-swing_2.11-1.0.1.jar;" & Text4.Text & "\libraries\org\scala-lang\scala-xml_2.11\1.0.2\scala-xml_2.11-1.0.2.jar;" & Text4.Text & "\libraries\lzma\lzma\0.0.1\lzma-0.0.1.jar;" & Text4.Text & "\libraries\java3d\vecmath\1.5.2\vecmath-1.5.2.jar;" & Text4.Text & "\libraries\net\sf\trove4j\trove4j\3.0.3\trove4j-3.0.3.jar;" & Text4.Text & "\libraries\org\apache\maven\maven-artifact\3.5.3\maven-artifact-3.5.3.jar;"
temp7 = "" & Text4.Text & "\versions\Modtester\Modtester.jar net.minecraft.launchwrapper.Launch --versionType PCL2 --username " & Text1.Text & " --version Modtester --gameDir " & Text4.Text & "\versions\Modtester --assetsDir " & Text4.Text & "\assets --assetIndex 1.12 --uuid fb48efcbb7014a6f883d5f5bdacda3dd --accessToken eyJhbGciOiJIUzI1NiJ9.eyJ4dWlkIjoiMjUzNTQwODUzNzA4NSMxNTIsImFnZyI6IkFkdWx0Iiwic3ViIjoiZmE1Njg1OTEtYWZhZS00Zjc5LWEzNjgtMjFiYTYxMmIwYjAyIiwibmJmIjoxNjY0NjE1NzQ2LCJhdXRoIjoiWEJPWCIsInJvbGVzIjpbXSwiaXNzIjoiYXV0aGVudGljYXRpb24iLCJleHAiOjE2NjQ3MDIxNDYsImlhdCI6MTY2NDYxNTc0NiwicGxhdGZvcm0iOiJVTktOT1dOIiwieXVpZCI6IjU2MTlkN2ZhYThmNjI4MWQwZmVjODY5ZjE1MzQwMzg0In0.PIBqz5e-Oq64-zcsPegxC6P0FXJWLz8noDQKYb2h0bQ --userType Mojang --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --versionType Forge --height " & Text8.Text & " --width " & Text7.Text & " " & Chr(10) & "" & Chr(13) & "Echo End" & Chr(10) & "" & Chr(13) & "pause " & Chr(10) & "" & Chr(13) & "" & Text2.Text & "\launcher.exe"
code = temp1 + temp2 + temp3 + temp4 + temp5 + temp6 + temp7
Print #1, code
Close #1
Me.Hide
End Sub

Private Sub Command2_Click()
Open "" & App.Path & "\property.txt" For Output As #1
Print #1, Text5.Text
Close #1
Me.Hide
End Sub

Private Sub Form_Load()
On Error GoTo Err:
Text2.Text = "" & App.Path & ""
Open "" & App.Path & "\property.txt" For Binary As #1
Text5 = StrConv(InputB$(LOF(1), 1), vbUnicode)
Close #1
Open "" & App.Path & "\property.txt" For Input As #2
While Not EOF(2)
Line Input #2, a
Line Input #2, b
Line Input #2, c
Line Input #2, d
Line Input #2, e
Wend
Close #2
Text1.Text = a
Text3.Text = c
Text4.Text = b
Text7.Text = d
Text8.Text = e

If Text3.Text = "" Then
    If Dir("C:\ProgramData\Oracle\Java\javapath\javaw.exe") = "" Then
        MsgBox "未检测到java", vbCritical, "尽管我们不排除误报"
    Else
        Text3.Text = "C:\ProgramData\Oracle\Java\javapath\javaw.exe"
    End If
Else
    
End If

Err:
Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\property.txt")
Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\start.bat")
End Sub

Private Sub Text3_DblClick()
Text3.Text = "C:\ProgramData\Oracle\Java\javapath\javaw.exe"
End If
End Sub

Private Sub Text4_DblClick()
Text4.Text = "" & App.Path & "\.minecraft"
If Dir("" & Text4.Text & "") = "" Then
Form3.Show
Else
End If
End Sub

Private Sub Text7_DblClick()
Text7.Text = Screen.Width / Screen.TwipsPerPixelX
Text8.Text = Screen.Height / Screen.TwipsPerPixelY
End Sub

Private Sub Text8_DblClick()
Text7.Text = Screen.Width / Screen.TwipsPerPixelX
Text8.Text = Screen.Height / Screen.TwipsPerPixelY
End Sub
