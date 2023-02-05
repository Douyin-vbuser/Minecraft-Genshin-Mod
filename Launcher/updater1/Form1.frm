VERSION 5.00
Begin VB.Form Form1 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Updater1"
   ClientHeight    =   750
   ClientLeft      =   45
   ClientTop       =   390
   ClientWidth     =   8640
   Icon            =   "Form1.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   750
   ScaleWidth      =   8640
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.Timer Timer2 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   6840
      Top             =   240
   End
   Begin VB.Timer Timer1 
      Enabled         =   0   'False
      Interval        =   1000
      Left            =   7680
      Top             =   240
   End
   Begin VB.Label Label1 
      Caption         =   "更新过程(仅供测试):读取配置文件;生成更新指令;执行指令"
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
    
    Open "" & App.Path & "\update.bat" For Output As #1
    code = "del " & b & "\versions\Modtester\mods\genshin.jar" & Chr(10) + Chr(13) & "move " & App.Path & "\genshin.jar " & b & "\versions\Modtester\mods\genshin.jar" & Chr(10) + Chr(13) & "taskkill /f /im launcher.exe /t" & Chr(10) + Chr(13) & "del " & App.Path & "\launcher.exe" & Chr(10) + Chr(13) & "ren " & App.Path & "\launcher1.exe launcher.exe" & Chr(10) + Chr(13) & "->result.txt"
    Print #1, code
    Close #1
    Timer1.Enabled = True
End If
End Sub

Private Sub Timer1_Timer()
If Dir("" & App.Path & "\update.bat") = "" Then
    Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);生成更新指令;执行指令"
Else
    Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);生成更新指令(成功);执行指令"
    Shell ("" & App.Path & "/update.bat")
    Timer2.Enabled = True
    Timer1.Enabled = False
End If
End Sub

Private Sub Timer2_Timer()
If Dir("" & App.Path & "\result.txt") = "" Then
    Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);生成更新指令(成功);执行指令"
Else
    Label1.Caption = "更新过程(仅供测试):读取配置文件(成功);生成更新指令(成功);执行指令(成功)"
    
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
        MsgBox "存在更新的版本", vbInformation, "Update Manager"
        'TODO：修改为预留的更新的更新网址
    End If
    Timer2.Enabled = False
End If
End Sub
