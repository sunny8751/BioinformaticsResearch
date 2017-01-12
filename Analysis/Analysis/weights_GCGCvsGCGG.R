#7/28/2015
#weights_GCGCvsGCGG
#Sunwoo Yim
#graph the 13mer feature weights of GCGC core and GCGG core on xy plot
#
#clean everything
rm(list=ls())
#set the working database
setwd("/Users/Sunwoo/Documents/NCSSM/Research in Computational Science/Research/Data/Actual/Analysis/weights_E2F1_GCGCvsGCGG")
#
#read in the data and attach it (make available for analysis)
tf <- read.csv(file = "tf_E2F1_RGnormalized.csv", header = T)
#create the graph
plot(tf$GCGG~tf$GCGC, main="E2F1 1&3mer RGNorm Feats Cores GCGC vs. GCGG",
     xlab = "Core GCGC 1&3mer Feature Weights", ylab = "Core GCGG 1&3mer Feature Weights")
# ps.fit<-lm(tf$GCGG~tf$GCGC)
#print the best fit line
#abline(ps.fit)
# summary(ps.fit)
#print the y=x line
abline(0, 1)
#print error bars
#x is e2f1, y is e2f4
segments(tf$GCGC, tf$GCGG-tf$GCGGerrors,tf$GCGC, tf$GCGG+tf$GCGGerrors)
segments(tf$GCGC-tf$GCGCerrors, tf$GCGG,tf$GCGC+tf$GCGCerrors, tf$GCGG)
#
#identify values
# identify(tf$e2f1averages, tf$e2f4averages)
# epsilon = 0.01
# segments(e2f1$averages-epsilon,e2f4$averages-e2f4$errors,e2f1$averages+epsilon,e2f4$averages-e2f4$errors)
# segments(e2f1$averages-epsilon,e2f4$averages+e2f4$errors,e2f1$averages+epsilon,e2f4$averages+e2f4$errors)
#