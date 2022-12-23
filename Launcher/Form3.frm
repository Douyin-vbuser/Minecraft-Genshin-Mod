VERSION 5.00
Object = "{EAB22AC0-30C1-11CF-A7EB-0000C05BAE0B}#1.1#0"; "ieframe.dll"
Begin VB.Form Form3 
   BorderStyle     =   3  'Fixed Dialog
   Caption         =   "Download"
   ClientHeight    =   4575
   ClientLeft      =   45
   ClientTop       =   390
   ClientWidth     =   5385
   LinkTopic       =   "Form3"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   4575
   ScaleWidth      =   5385
   ShowInTaskbar   =   0   'False
   StartUpPosition =   3  '窗口缺省
   Begin VB.Frame Frame2 
      Caption         =   "版本号"
      Height          =   2295
      Left            =   120
      TabIndex        =   9
      Top             =   2160
      Width           =   5055
      Begin VB.TextBox Text4 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   17
         Text            =   "(none)"
         Top             =   1800
         Width           =   3615
      End
      Begin VB.TextBox Text3 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   16
         Text            =   "basic 1.0.6"
         Top             =   1320
         Width           =   3615
      End
      Begin VB.TextBox Text2 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   15
         Text            =   "alpha 1.1.7"
         Top             =   840
         Width           =   3615
      End
      Begin VB.TextBox Text1 
         Height          =   270
         Left            =   1080
         Locked          =   -1  'True
         TabIndex        =   14
         Text            =   "release 1.0.0"
         Top             =   360
         Width           =   3615
      End
      Begin VB.Label Label7 
         Caption         =   "地图："
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
         TabIndex        =   13
         Top             =   1800
         Width           =   1455
      End
      Begin VB.Label Label5 
         Caption         =   "模组："
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
         TabIndex        =   12
         Top             =   1320
         Width           =   1455
      End
      Begin VB.Label Label4 
         Caption         =   "启动器："
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
         TabIndex        =   11
         Top             =   840
         Width           =   1455
      End
      Begin VB.Label Label3 
         Caption         =   "运行库："
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
         TabIndex        =   10
         Top             =   360
         Width           =   1455
      End
   End
   Begin VB.Frame Frame1 
      Caption         =   "下载"
      Height          =   1935
      Left            =   120
      TabIndex        =   0
      Top             =   120
      Width           =   5055
      Begin VB.CommandButton Command3 
         Caption         =   "检查更新"
         Height          =   375
         Left            =   1560
         TabIndex        =   8
         Top             =   1320
         Width           =   975
      End
      Begin VB.CommandButton Command2 
         Caption         =   "部署"
         Height          =   375
         Left            =   3720
         TabIndex        =   7
         Top             =   1320
         Width           =   975
      End
      Begin VB.CommandButton Command1 
         Caption         =   "下载"
         Height          =   375
         Left            =   2640
         TabIndex        =   5
         Top             =   1320
         Width           =   975
      End
      Begin VB.ComboBox Combo2 
         Height          =   300
         ItemData        =   "Form3.frx":0000
         Left            =   1560
         List            =   "Form3.frx":0007
         TabIndex        =   4
         Text            =   "Github"
         Top             =   840
         Width           =   3135
      End
      Begin VB.ComboBox Combo1 
         Height          =   300
         ItemData        =   "Form3.frx":0013
         Left            =   1560
         List            =   "Form3.frx":0023
         TabIndex        =   2
         Text            =   "Minecraft libraries"
         Top             =   360
         Width           =   3135
      End
      Begin VB.Label Label2 
         Caption         =   "选择下载源："
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
         TabIndex        =   3
         Top             =   840
         Width           =   1455
      End
      Begin VB.Label Label1 
         Caption         =   "选择下载项："
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
         Width           =   1455
      End
   End
   Begin SHDocVwCtl.WebBrowser WebBrowser1 
      Height          =   1215
      Left            =   120
      TabIndex        =   6
      Top             =   360
      Width           =   5055
      ExtentX         =   8916
      ExtentY         =   2143
      ViewMode        =   0
      Offline         =   0
      Silent          =   0
      RegisterAsBrowser=   0
      RegisterAsDropTarget=   1
      AutoArrange     =   0   'False
      NoClientEdge    =   0   'False
      AlignLeft       =   0   'False
      NoWebView       =   0   'False
      HideFileNames   =   0   'False
      SingleClick     =   0   'False
      SingleSelection =   0   'False
      NoFolders       =   0   'False
      Transparent     =   0   'False
      ViewID          =   "{0057D0E0-3573-11CF-AE69-08002B2E1262}"
      Location        =   ""
   End
End
Attribute VB_Name = "Form3"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Declare Function URLDownloadToFile Lib "urlmon.dll" Alias "URLDownloadToFileA" (ByVal pCaller As Long, ByVal szURL As String, ByVal szFileName As String, ByVal dwReserved As Long, ByVal lpfnCB As Long) As Long
Private Sub Command1_Click()
If Combo1.Text = "Minecraft libraries" Then
WebBrowser1.Navigate "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/Runtime_environment/default.minecraft.exe"
Else
End If
End Sub

Private Sub Command2_Click()
a = MsgBox("防止误操作导致崩溃,确认部署", vbYesNo, "Update Manager")
If a = vbYes Then
    If Combo1.Text = "Minecraft libraries" Then
        MsgBox "按默认执行解压程序即可", vbInformation, "Update Manager"
        Shell ("" & App.Path & "\.minecraft.exe")
    Else
        If Combo1.Text = "Map" Then
        
        Else
            If Combo1.Text = "Mod" Then
            
            Else
            
            End If
        End If
    End If
End If
End Sub

Private Sub Command3_Click()
Dim nUrl As String, F As String, S As Long
nUrl = "https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/versions_check/versions.txt": F = App.Path & "\versions.txt"
S = URLDownloadToFile(0, nUrl, F, 0, 0)
Open "" & App.Path & "\versions.txt" For Input As #3
While Not EOF(3)
Line Input #3, a
Line Input #3, b
Line Input #3, c
Line Input #3, d
Wend
Close #3

If a = Text1.Text Then
    If b = Text2.Text Then
        If c = Text3.Text Then
            If d = Text4.Text Then
                MsgBox "所有文件已是最新", vbInformation, "Update Manager"
            Else
                MsgBox "地图文件可更新", vbInformation, "Update Manager"
            End If
        Else
            MsgBox "模组可更新", vbInformation, "Update Manager"
        End If
    Else
        MsgBox "启动器可更新", vbInformation, "Update Manager"
    End If
Else
    MsgBox "运行库可更新", vbInformation, "Update Manager"
End If

Shell ("C:\Windows\System32\cmd.exe /c del " & App.Path & "\versions.txt")
End Sub
