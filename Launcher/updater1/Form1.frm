VERSION 5.00
Begin VB.Form Form1 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Updater1"
   ClientHeight    =   750
   ClientLeft      =   45
   ClientTop       =   390
   ClientWidth     =   8640
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   750
   ScaleWidth      =   8640
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.Label Label1 
      Caption         =   "更新过程(仅供测试):读取配置文件;替换模组文件;替换启动器"
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
      Left            =   120
      TabIndex        =   0
      Top             =   240
      Width           =   8415
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Declare Function URLDownloadToFile Lib "urlmon.dll" Alias "URLDownloadToFileA" (ByVal pCaller As Long, ByVal szURL As String, ByVal szFileName As String, ByVal dwReserved As Long, ByVal lpfnCB As Long) As Long

Private Sub Form_Load()
If Dir("" & App.Path & "\property.txt") = "" Then
    Label1.Caption = "更新失败,未能读取配置文件,请确认更新包位于启动器目录中或配置文件是否存在"
Else
    Open "" & App.Path & "\property.txt" For Input As #2
    While Not EOF(2)
    Line Input #2, a
    Line Input #2, b
    Line Input #2, c
    Line Input #2, d
    Line Input #2, e
    Wend
    Close #2
    
    Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);替换模组文件;替换启动器"
        Shell ("C:\Windows\System32\cmd.exe /c del " & b & "\versions\Modtester\mods\genshin.jar")
        Shell ("C:\Windows\System32\cmd.exe /c copy " & App.Path & "\genshin.jar " & b & "\versions\Modtester\mods\genshin.jar")
        Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);替换模组文件(已执行);替换启动器"
        Shell ("C:\Windows\System32\cmd.exe /c taskkill /f /im launcher.exe /t")
        Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\launcher.exe")
        Shell ("C:\Windows\System32\cmd.exe /c ren " & App.Path & "\launcher1.exe launcher.exe")
        
        Dim nUrl As String, F As String, S As Long
        nUrl = "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/version-check/versions.txt": F = App.Path & "\versions.txt"
        S = URLDownloadToFile(0, nUrl, F, 0, 0)
        Open "" & App.Path & "\versions.txt" For Input As #3
        While Not EOF(3)
        Line Input #3, a
        Line Input #3, b
        Line Input #3, c
        Line Input #3, d
        Wend
        Close #3
        Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\versions.txt")
        If a = "release 1.0.0" And b = "alpha 1.1.7" And c = "basic 1.0.6" And d = "(none)" Then
            MsgBox "更新完成", vbInformation, "Updater1"
            Shell ("" & App.Path & "\launcher.exe")
        Else
            MsgBox "存在更新的版本", vbInformation, "TODO"
        End If
End If
End Sub
